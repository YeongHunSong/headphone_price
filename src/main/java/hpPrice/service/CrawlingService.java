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

    public static final String DC_GALL_NAME = "sff";
    public static final String DC_GALL_URL = "https://gall.dcinside.com/mgallery/board/lists/?id=" + DC_GALL_NAME;
    public static final String DC_POST_URL = "https://gall.dcinside.com/mgallery/board/view/?id=" + DC_GALL_NAME;
    public static final String DC_TAB_PAGE = "&search_head=40&page=";
    public static final String DC_POST_NUM = "&no=";

    public static final String DC_IMG_HOST_BEFORE = "dcimg8"; // dcimg6 / dcimg8 / dcimg9 // TODO 여기 있는 호스트를 전부 아래 호스트로 변경하기
    public static final String DC_IMG_HOST_AFTER = "dcimg3"; // dcimg1 / dcimg2 / dcimg3 / dcimg4 / dcimg7
    public static final String DC_CON_HOST_BEFORE = "dcimg5";
    public static final String DC_CON_HOST_AFTER = "dcimg1"; // 디시콘은 dcimg1만 가능

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final int MAX_RETRY_COUNT = 4;
    public static final int TIME_OUT = 30000;
    public static final int SLEEP_TIME = 450;

    // TODO TEST 용도
    public static int LAST_PAGE = 30;


    @Scheduled(fixedDelay = 60000) // 60초 주기로 크롤링
    public void parsingDcPost() {
        log.info("크롤링 동작 중 [{}]", LocalDateTime.now().format(DATE_FORMATTER));

        // TODO 에러로 종료된 경우 거기서부터 다시 DB 저장할 수 있도록 로직
        long nowPostNum = 0L; // TODO 이 코드도 변경 혹은 삭제
        int startPage = 1;

        parsing:
        try {
//            LAST_PAGE = lastPageSearch(); // DB가 업데이트 되어있는 상황에서는 굳이 lastPage 값을 구하기 위해 커넥션을 할 필요는 없는듯.
            Long dbLastPostNum = postService.lastPostNum();
            Long errorNum = postService.errorCheck();

            errorCheck:
            if (errorNum != null) { // ERROR 발생했는지 확인
                log.info("ERROR 복구 모드");
                for (int page = 1; page <= LAST_PAGE; page++) {
                    for (Element postTitle : parsing(DC_GALL_URL + DC_TAB_PAGE + page, ".gall_list .us-post")) {
                        Long postNum = Long.valueOf(postTitle.selectFirst(".gall_num").text());
                        if (nowPostNum == postNum) { // 막 에러가 발생한 postNum 과 일치하는지 확인 TODO while 제거 했기 때문에 로직 변경해야함.
                            postService.resolveError(postNum, errorNum);
                            startPage = page; // ERROR 발생한 page 찾아서 반환
                            log.info("ERROR 발생 페이지 = [" + startPage + " page]");
                            break errorCheck;
                        }
                    }
                }
            }

            // TODO ERROR 발생한 페이지는 찾았는데, 해당 페이지에서 다시 데이터 저장을 시작하면 DB 중복값으로 Exception 발생함. 이거는 어떻게 해결할까?
            pageLoop:
            for (int page = startPage; page <= LAST_PAGE; page++) { // TODO LAST_PAGE ==> lastPage
                // ### 게시글 리스트 파싱 ###
                log.info("현재 페이지 -> [{}]page", page);
                listParsing:
                for (Element postTitle : parsing(DC_GALL_URL + DC_TAB_PAGE + page, ".gall_list .us-post")) {
                    nowPostNum = Long.parseLong(postTitle.selectFirst(".gall_num").text());

                    // TODO 여기 로직을 변경해서 ERROR 복구 방법 찾기 ####################
                    if (dbLastPostNum == nowPostNum // DB 마지막 저장값과 파싱값이 동일. (일반적인 경우)
                            || dbLastPostNum > nowPostNum) { // DB 마지막 저장값보다 파싱값이 작음. (마지막 저장값이 삭제된 경우)
                        break parsing; // 필요한 게시글까지 다 받아온 경우
                    }
                    saveList(postTitle);

                    // ### 게시글 내용 파싱 ### // TODO log 남기기 // #############
                    Elements post = parsing(DC_POST_URL + DC_POST_NUM + nowPostNum, ".write_div > *");
                    if (post.isEmpty()) throw new IOException("게시글 받아오기 실패 \n실패한 게시글 번호 => " + nowPostNum);
                    post.removeIf(postLine -> postLine.select("iframe").is("iframe")); // 동영상 링크 삭제
                    for (Element postLine : post) imageUrlCheck(postLine);
                    savePost(nowPostNum, post);
                } // for listParsing
            } // for pageLoop
        } catch (IOException e) { // 타임아웃, 데이터 없음, 500에러(HttpStatusException) 등등
            postService.errorReport(ErrorPost.errorReport(nowPostNum, e.toString()));
            log.error("IOException -> ", e);
        } catch (NullPointerException e) {
            log.error("NullPointerException -> ", e);
        } catch (InterruptedException e) {
            log.error("sleep 실패 -> ", e);
        } catch (Exception e) {
            log.error("Exception -> ", e);
        } // catch
        log.info("parsing 종료");
    } // method



    private Elements getElements(String connectUrl, String selectQuery) throws IOException {
        return Jsoup.connect(connectUrl)
                .userAgent(USER_AGENT)
                .timeout(TIME_OUT)
                .get()
                .select(selectQuery);
    }

    private Elements parsing(String connectUrl, String selectQuery) throws IOException, InterruptedException {
        for (int tryCount = 0; tryCount < MAX_RETRY_COUNT; tryCount++) {
            try {
                sleep(SLEEP_TIME);
                return getElements(connectUrl, selectQuery);
            } catch (IOException e) {
                if (tryCount == MAX_RETRY_COUNT - 1) throw e;
                log.info("재시도 중 => {}회", tryCount);
                sleep(SLEEP_TIME * (tryCount + 1));
            }
        }
        throw new IOException("페이지 로드 실패");
    }

    private int lastPageSearch() throws IOException, InterruptedException {
        String pageUrl = parsing(DC_GALL_URL + DC_TAB_PAGE, ".page_end")
                        .attr("href"); // 파싱 태그값 변경된 경우, URL 변경된 경우 NPE 발생
        return Integer.parseInt(pageUrl.substring(pageUrl.indexOf("page=") + 5, pageUrl.indexOf("&search")));
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

    private void savePost(Long postNum, Elements post) {
        postService.newPostDC(
                Post.newPost(
                        postNum,
                        post.outerHtml()));
    }

    private void imageUrlCheck(Element postLine) throws IOException {
        Elements image = postLine.select("img");
        String imageUrl = image.attr("src"); // null X

        // dc 이미지 ex) https://dcimg3.dcinside.co.kr/viewimage.php?id=23b8c72ee0d33cb666b0d8b0&amp;no=24b0d769e
        // dc 디시콘 ex)
        if (imageUrl.contains("dcimg")) { // 이미지가 있는지 확인 후 DC 이미지의 경우 외부에서 불러올 수 있는 이미지 HOST 변경
            switch ((imageUrl.charAt(13))) { // TODO host 번호로 찾지말고 URL 안에 다른 값으로 찾기
                case '1': // 이미지 링크 O
                    break;
                case '3': // 이미지 링크 O
                    break;
                case '4': // 이미지 링크 O
                    break;
                case '5': // 디시콘 링크 X
                    image.attr("src", imageUrl.replace(DC_CON_HOST_BEFORE, DC_CON_HOST_AFTER));
                    break;
                case '8': // 이미지 링크 X
                    image.attr("src", imageUrl.replace(DC_IMG_HOST_BEFORE, DC_IMG_HOST_AFTER));
                    break;
                default:
                    throw new IOException("이상한 이미지 URL => " + imageUrl);
            }
        }
    }

}