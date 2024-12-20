package hpPrice.service;

import hpPrice.domain.ErrorPost;
import hpPrice.domain.Post;
import hpPrice.domain.PostList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Thread.sleep;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingService {

    private final PostService postService;

    public static final String DC_HEADPHONE_LIST_URL = "https://gall.dcinside.com/mgallery/board/lists/?id=newheadphone&search_head=120";
    public static final String DC_HEADPHONE_POST_URL = "https://gall.dcinside.com/mgallery/board/view/?id=newheadphone&no=";
    public static final String DC_IMG_HOST_BEFORE = "dcimg8"; // dcimg1 / dcimg5 / dcimg6 / dcimg8 / dcimg9 // TODO 여기 있는 호스트를 전부 아래 호스트로 변경하기
    public static final String DC_IMG_HOST_AFTER = "dcimg3"; // dcimg2 / dcimg3 / dcimg4 / dcimg7
    public static final String DC_CON_HOST_BEFORE = "dcimg5";
    public static final String DC_CON_HOST_AFTER = "dcimg1"; // 디시콘은 dcimg1만 가능
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final int MAX_RETRY_COUNT = 3;
    public static final int TIME_OUT = 30000;
    public static final int SLEEP_TIME = 450;

    // TODO TEST 용도
    public static int LAST_PAGE = 25;

    // TODO 로딩 실패 시 재시도 로직 구현하기
                    /*
                        public Document getDocumentWithRetry(String url, int maxRetries) throws IOException {
                                for (int i = 0; i < maxRetries; i++) {
                                    try {
                                        return Jsoup.connect(url).get();
                                    } catch (IOException e) {
                                        if (i == maxRetries - 1) throw e;
                                        Thread.sleep(1000 * (i + 1)); // 점진적으로 대기 시간 증가
                                    }
                                }
                                throw new IOException("Failed to load document after " + maxRetries + " retries");
                            }
                     */


    @Scheduled(fixedDelay = 60000) // 60초 주기로 크롤링
    public void parsingDcPost() {
        log.info("크롤링 동작 중 [{}]", LocalDateTime.now().format(DATE_FORMATTER));
//        LAST_PAGE = lastPageSearch(); // DB가 업데이트 되어있는 상황에서는 굳이 lastPage 값을 구하기 위해 커넥션을 할 필요는 없는듯.

        // TODO ERROR 발생한 게시글 번호 저장할 수 있는 테이블 만들기
        // TODO 에러로 종료된 경우 거기서부터 다시 DB 저장할 수 있도록 로직
        long tryCount = 0L;
        long errorPostNum = 0L; // 개꿀
        int startPage = 1;

        parsing:
        while (tryCount < MAX_RETRY_COUNT) {
            try {
                Long dbLastPostNum = postService.lastPostNum();

                if (postService.errorCheck() != null) { // TODO 에러 table 에 값이 있으면 해당 값이 어느 페이지에 있는지 찾기


                    for (int i = 1; i <= LAST_PAGE; i++) {
                        sleep(SLEEP_TIME);
                        Elements dcHeadphonePostList = Jsoup.connect(DC_HEADPHONE_LIST_URL + "&page=" + i)
                                .userAgent(USER_AGENT)
                                .timeout(TIME_OUT)
                                .get()
                                .select(".gall_list .us-post");

                        for (Element postTitle : dcHeadphonePostList) {
                            Element postNum = postTitle.selectFirst(".gall_num");

                            if (postService.errorCheck() == Long.parseLong(postNum.toString())) {
                                // TODO errorPostNum 이용하기
                            }
                        }
                    }
                }



                pageLoop:
                for (int i = startPage; i <= LAST_PAGE; i++) { // TODO LAST_PAGE ==> lastPage
                    // ### 게시글 리스트 파싱 ###
                    sleep(SLEEP_TIME);
                    Elements dcHeadphonePostList = Jsoup.connect(DC_HEADPHONE_LIST_URL + "&page=" + i)
                            .userAgent(USER_AGENT)
                            .timeout(TIME_OUT)
                            .get()
                            .select(".gall_list .us-post");

                    log.info("현재 페이지 -> [{}]page", i);
                    listParsing:
                    for (Element postTitle : dcHeadphonePostList) {
                        Element postNum = postTitle.selectFirst(".gall_num");

                        if (dbLastPostNum == Long.parseLong(postNum.text()) // DB 마지막 저장값과 파싱값이 동일. (일반적인 경우)
                                || dbLastPostNum > Long.parseLong(postNum.text())) { // DB 마지막 저장값보다 파싱값이 작음. (마지막 저장값이 삭제된 경우)
//                            log.info("DB 갱신 완료: 페이지 -> [{}]page", i);
                            break parsing; // 필요한 게시글까지 다 받아온 경우
                        }

                        saveList(postTitle);

                        // ### 게시글 내용 파싱 ###
                        // TODO log 남기기
                        errorPostNum = Long.parseLong(postNum.text());
                        sleep(SLEEP_TIME);
                        Elements dcHeadphonePost = Jsoup.connect(DC_HEADPHONE_POST_URL + postNum.text())
                                .userAgent(USER_AGENT)
                                .timeout(TIME_OUT)
                                .get()
                                .select(".write_div > *");

                        if (dcHeadphonePost.isEmpty()) { // 게시글 내용 받아오기 실패
                            throw new IOException("게시글 받아오기 실패 \n실패한 게시글 번호 => " + postNum);
                        }

                        dcHeadphonePost.removeIf(postLine -> postLine.select("iframe").is("iframe")); // 업로드 동영상 링크 삭제

                        postParsing:
                        for (Element postLine : dcHeadphonePost) {
                            Elements image = postLine.select("img");
                            String imageUrl = image.attr("src"); // null X


                            // dc 이미지 ex) https://dcimg3.dcinside.co.kr/viewimage.php?id=23b8c72ee0d33cb666b0d8b0&amp;no=24b0d769e
                            // dc 콘    ex)

                            if (imageUrl.contains("dcimg")) { // 이미지가 있는지 확인 후 DC 이미지의 경우 외부에서 불러올 수 있는 이미지 HOST 변경
                                switch ((imageUrl.charAt(13))) { // TODO host 번호로 찾지말고 URL 안에 다른 값으로 찾기
                                    case '3': // 업로드 이미지 링크
                                        break;
                                    case '4': // 업로드 이미지 링크
                                        break;
                                    case '5': // 디시콘 이미지 링크
                                        image.attr("src", imageUrl.replace(DC_CON_HOST_BEFORE, DC_CON_HOST_AFTER));
                                        break;
                                    case '8': // 업로드 이미지 링크
                                        image.attr("src", imageUrl.replace(DC_IMG_HOST_BEFORE, DC_IMG_HOST_AFTER));
                                        break;
                                    default:
                                        throw new IOException("이상한 URL => " + imageUrl);
                                }
                            }
                        } // for postParsing

                        savePost(postNum, dcHeadphonePost);


                    } // for listParsing
                } // for pageLoop
            } catch (IOException e) { // 타임아웃, 데이터 없음, 500에러(HttpStatusException) 등등
                // TODO errorPostNum
                tryCount++;
                postService.errorReport(ErrorPost.errorReport(errorPostNum, e.toString()));
                log.error("IOException -> ", e);
            } catch (NullPointerException e) {
                log.error("NullPointerException -> ", e);
            } catch (InterruptedException e) {
                log.error("sleep 실패 -> ", e);
            } catch (Exception e) {
                log.error("Exception -> ", e);
            } // catch
        } // while parsing
        if (tryCount >= 3) {
            log.info("재시도 횟수 초과");
        } else {
            log.info("parsing 종료");
        }
    } // method




    private int lastPageSearch() {
        try {
            String pageUrl = Jsoup.connect(DC_HEADPHONE_LIST_URL)
                    .userAgent(USER_AGENT)
                    .timeout(TIME_OUT)
                    .get() // post()
                    .selectFirst(".page_end").attr("href"); // 파싱 태그값 변경된 경우, URL 변경된 경우 NPE 발생

            return Integer.parseInt(pageUrl.substring(pageUrl.indexOf("page=") + 5, pageUrl.indexOf("&search")));

        } catch (IOException e) {
            log.error("크롤링 실패 -> ", e);
        } catch (NullPointerException e) {
            log.error("파싱 태그 변경 필요 -> ", e);
        } catch (Exception e) {
            log.error("Exception -> ", e);
        }
        return 0;
    }

    private void saveList(Element element) {
        postService.newPostListDC(
                PostList.newPostList(
                        Long.valueOf(element.selectFirst(".gall_num").text()),
                        element.selectFirst(".gall_tit [href]").text(),
                        element.selectFirst(".gall_tit a").absUrl("href"),
                        element.selectFirst(".nickname em").text(),
                        element.selectFirst(".gall_writer").attr("data-uid"),
                        LocalDateTime.parse(element.selectFirst(".gall_date").attr("title"), CrawlingService.DATE_FORMATTER)));
    }

    private void savePost(Element postNum, Elements dcHeadphonePost) {
        postService.newPostDC(
                Post.newPost(
                        Long.valueOf(postNum.text()),
                        dcHeadphonePost.outerHtml()));
    }
}
