package hpPrice.service.crawlingAndParsing;

import hpPrice.common.dateTime.DateTimeUtils;
import hpPrice.domain.ErrorPost;
import hpPrice.domain.Post;
import hpPrice.domain.PostItem;
import hpPrice.domain.ErrorDto;
import hpPrice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static hpPrice.common.CommonConst.*;



@Slf4j
@Service
@RequiredArgsConstructor
public class DcGallCrawlingService {
    private final PostService postService;

    private static long postNum = 0;

//    @Scheduled(fixedDelay = 90000) // 60초 주기로 크롤링
    public void parsingDcPost() {
        log.info("크롤링 동작 중 [{}]", DateTimeUtils.getCurrentDateTime());

        parsingLogic:
        try {
            ErrorDto errorCheck = postService.errorCheck();
            long latestPostNum = postService.latestPostNum();
            int lastPage = findLastPage(); // 해당 탭 게시글 마지막 페이지 값 받이오기
            int startPage = findErrorPage(errorCheck, lastPage); // ERROR 발생했는지 확인 후 발생했었다면 ERROR 발생한 페이지 반환, 발생하지 않았었다면 1 반환.

            for (int page = startPage; page <= lastPage; page++) { log.info("현재 페이지 -> [{}]page", page);
                // ### POST_ITEM 파싱 ###
                for (Element postItem : connectAndParsing(DC_GALL_URL + DC_TAB_PAGE_QUERY + page, ".gall_list .us-post")) {
                    postNum = Long.parseLong(postItem.selectFirst(".gall_num").text());
                    if (errorCheck != null && postNum > errorCheck.getPostNum()) continue; // 에러 복구 시 에러 발생한 페이지에서 이미 저장된 게시글 넘김
                    if (latestPostNum == postNum || (errorCheck == null && latestPostNum > postNum)) break parsingLogic;
                    // DB 마지막 저장값과 파싱값이 동일. (일반적인 경우) || 에러 복구 모드가 아닌데, DB 마지막 저장값보다 파싱값이 작음. (마지막 저장값에 해당하는 게시글이 삭제된 경우)

                    // ### POST 파싱 ###
                    Elements post = connectAndParsing(DC_POST_URL + DC_POST_NUM_QUERY + postNum, ".write_div > *");
                    post.removeIf(postLine -> postLine.select("iframe").is("iframe")); // 동영상 링크 삭제
                    for (Element postLine : post) imageUrlCheck(postLine);

                    savePost(postItem, post);
                }
            }
        } catch (IOException e) { // 타임아웃, 데이터 없음, 500에러(HttpStatusException) 등등
            postService.reportError(ErrorPost.reportError(postNum, e.toString()));
            log.error("크롤링 사이트 연결 관련 Exception -> ", e);
        } catch (Exception e) {
            log.error("Exception 발생 -> ", e);
        }
        log.info("parsing 종료");
    }

    public void parsingNaverCafePostList() {
        for (int page = 1; page <= 10; page++) {

        }
    }




    // ### 코드 정리용 추출 메서드 ###

    public Elements connectAndParsing(String connectUrl, String selectQuery) throws IOException, InterruptedException {
        for (int tryCount = 0; tryCount < MAX_RETRY_COUNT; tryCount++) {
            try {
                Thread.sleep(SLEEP_TIME);
                Elements parsingData = Jsoup.connect(connectUrl)
                        .userAgent(USER_AGENT)
                        .timeout(TIME_OUT)
                        .get()
                        .select(selectQuery);
                if (parsingData.isEmpty()) throw new IOException("화이트 페이지 에러 발생 " + connectUrl);
                return parsingData;
            } catch (IOException e) {
                if (tryCount == MAX_RETRY_COUNT - 1) throw e;
                log.info("에러 발생으로 인한 재시도 횟수 => {}회", tryCount + 1);
                log.info("에러 발생한 URL = {}", connectUrl);
                Thread.sleep(SLEEP_TIME * (tryCount + 1));
            }
        }
        throw new IOException("페이지 로드 실패");
    }

    private int findLastPage() throws IOException, InterruptedException {
        String pageUrl = connectAndParsing(DC_GALL_URL + DC_TAB_PAGE_QUERY, ".page_end")
                .attr("href"); // 파싱 태그값 변경된 경우, URL 변경된 경우 NPE 발생
        return Integer.parseInt(pageUrl.substring(pageUrl.indexOf("page=") + 5, pageUrl.indexOf("&search")));
    }

    private int findErrorPage(ErrorDto errorCheck, int lastPage) throws IOException, InterruptedException {
        if (errorCheck != null) { log.info("ERROR 복구 모드");
            for (int errorPage = 1; errorPage <= lastPage; errorPage++) {
                for (Element postItem : connectAndParsing(DC_GALL_URL + DC_TAB_PAGE_QUERY + errorPage, ".gall_list .us-post")) {
                    if (errorCheck.getPostNum() == Long.parseLong(postItem.selectFirst(".gall_num").text())) {
                        postService.resolveError(errorCheck.getPostNum(), errorCheck.getErrorNum());
                        log.info("ERROR 발생 페이지 = [" + errorPage + " page]");
                        return errorPage;
                    }
                }
            }
        }
        return 1; // ERROR 없음
    }

    private void imageUrlCheck(Element postLine) throws IOException {
        Elements image = postLine.select("img");
        String imageUrl = image.attr("src"); // null X

        // dc 이미지 ex) https://dcimg3.dcinside.co.kr/viewimage.php?id=3ebbd6&amp;no=24b0d769e1d32ca73de886fa11d02831fdb1643214b32d9bb1b5ab3e58bb89b1216bffb62d1ebdb9618bca1b2397cbbb31cc16f365ff7ff1b8edce6f50754e1845bffcae
        // dc 디시콘 ex) https://dcimg5.dcinside.com/dccon.php?no=62b5df2be09d3ca567b1c5bc12d46b394aa3b1058c6e4d0ca41648b658eb26767ccfa27c0ebd239241422c8efa9616613c5f9cd0fac0542d864e3afb8ae684e5b94e57d1866f95ee58
        if (imageUrl.contains("dcimg")) { // 이미지가 있는지 확인 후 DC 이미지의 경우, 외부에서 불러올 수 없는 이미지 HOST 를 외부에서 불러올 수 있게 이미지 HOST 변경
            switch ((imageUrl.charAt(13))) { // TODO host 번호로 찾지말고 URL 안에 다른 값으로 찾기 (viewimage.php / dccon.php)
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


    // @Transactional
    // 동일 클래스에서 별도로 작성한 메서드에 어노테이션을 붙이는 걸로는 동작하지 않음, 접근 제어자는 public 한정.
    // 해결 방법 1. 호출할 상위 메서드에 어노테이션을 붙인다. (트랜잭션이 필요하지 않은 메서드들도 한 트랜젹션 내에 묶임. 내 로직의 경우 저장한 전체 데이터가 롤백되므로 안 됨.)
    // 해결 방법 2. 트랜잭션이 필요한 메서드를 별도의 클래스로 분리한 후 호출하는 방식을 이용한다. (트랜잭션이 필요한 부분만 트랜잭션으로 묶을 수 있음.)
    // 해결 방법 3. 자기 클래스에서 자기 클래스를 주입받아서 호출한다. (비추천)
    private void savePost(Element postItem, Elements content) {
        long postNum = Long.parseLong(postItem.selectFirst(".gall_num").text());
        String title = postItem.selectFirst(".gall_tit [href]").text();
        try {
            postService.newPostItemDC(
                    PostItem.newPostItem(
                            postNum,
                            title,
                            postItem.selectFirst(".gall_tit a").absUrl("href"),
                            postItem.selectFirst(".nickname em").text(),
                            postItem.selectFirst(".gall_writer").attr("data-uid"),
                            DateTimeUtils.parseDcDateTime(postItem.selectFirst(".gall_date").attr("title"))));
            postService.newPostDC(
                    Post.newPost(
                            postNum,
                            content.outerHtml()));
        } catch (DuplicateKeyException e) {
            postService.reportError(ErrorPost.reportError(postNum, e.toString()));
            postService.resolveError(postNum, postService.errorCheck().getErrorNum()); // 추가적인 복구가 필요하지는 않으므로 해결 처리.
            log.error("이미 저장된 게시글로 저장되지 않음 -> {} {} \n", postNum, title, e);
        } // 해당 페이지 내 마지막 글을 데이터베이스에 저장하던 도중 새로운 글이 작성되어 다음 페이지 첫번째 글로 넘어가면서 중복 오류가 발생한 건에 대해서만 예외 처리.
    }
}