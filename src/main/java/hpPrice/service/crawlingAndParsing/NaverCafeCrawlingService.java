package hpPrice.service.crawlingAndParsing;

import hpPrice.common.dateTime.DateTimeUtils;
import hpPrice.domain.ErrorPost;
import hpPrice.domain.NaverPostItem;
import hpPrice.domain.Post;
import hpPrice.domain.PostItem;
import hpPrice.repository.PostRepository;
import hpPrice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static hpPrice.common.CommonConst.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverCafeCrawlingService {
    private final PostService postService;
    private final PostRepository postRepository;
    private final NaverLoginCookieService naverLoginCookieService;

    private static final List<Integer> CATEGORY_NUMBER = new ArrayList<>(Arrays.asList(21, 102, 192, 42)); // 헤드폰, 이어폰, 덱엠, 케이블
    private static long postNum = 0;

    public void naverCafePostItemCrawling() {
        log.info("NAVER CAFE 크롤링 동작 중 [{}]", DateTimeUtils.getCurrentDateTime());

        parsingLogic:
        try {
//            ErrorDto errorCheck = errorCheck();
//            long latestPostNum = latestPostNum();
//            int startPage = findErrorPage(errorCheck, lastPage); // ERROR 발생했는지 확인 후 발생했었다면 ERROR 발생한 페이지 반환, 발생하지 않았었다면 1 반환.

            for (int category : CATEGORY_NUMBER) {
                for (int page = startPage; page <= 10; page++) {
                    log.info("현재 페이지 -> [{}]page", page);
                    // ### POST_ITEM 파싱 ###
                    Elements naverPostList = connectAndParsing(DC_GALL_URL + DC_TAB_PAGE_QUERY + page, "div.article-board > table > tbody > tr");
                    naverPostList.removeIf(postItem -> postItem.hasClass("board-notice")); // 공지글 제거

                    for (Element postItem : naverPostList) {
                        postNum = Long.parseLong(postItem.selectFirst(".inner_number").text());
                        if (errorCheck != null && postNum > errorCheck.getPostNum()) continue; // 에러 복구 시 에러 발생한 페이지에서 이미 저장된 게시글 넘김
                        if (latestPostNum == postNum || (errorCheck == null && latestPostNum > postNum)) break parsingLogic;
                        // DB 마지막 저장값과 파싱값이 동일. (일반적인 경우) || 에러 복구 모드가 아닌데, DB 마지막 저장값보다 파싱값이 작음. (마지막 저장값에 해당하는 게시글이 삭제된 경우)

                        // ### POST 파싱 ###
//                        Elements post = connectAndParsing(DC_POST_URL + DC_POST_NUM_QUERY + postNum, ".write_div > *");
//                        post.removeIf(postLine -> postLine.select("iframe").is("iframe")); // 동영상 링크 삭제
//                        for (Element postLine : post) imageUrlCheck(postLine);

                        savePostItem(postItem);
                    }
                }
            }

        } catch (IOException e) { // 타임아웃, 데이터 없음, 500에러(HttpStatusException) 등등
            reportError(ErrorPost.reportError(postNum, e.toString()));
            log.error("크롤링 사이트 연결 관련 Exception -> ", e);
        } catch (Exception e) {
            log.error("Exception 발생 -> ", e);
        }
        log.info("parsing 종료");
    }

    private void savePost(Element postItem, Elements content) {
        long postNum = Long.parseLong(postItem.selectFirst(".gall_num").text());
        String title = postItem.selectFirst(".gall_tit [href]").text();
        try {
            savePostItemNAVER(
                    NaverPostItem.newPostItem(
                            postNum,
                            title,
                            "https://cafe.naver.com/drhp/" + postNum,
                            postItem.selectFirst(".nickname em").text(),
                            postItem.selectFirst(".mem-level").html(),
                            DateTimeUtils.parseNaverDateTime(postItem.selectFirst(".td_date").text())));
        } catch (DuplicateKeyException e) {
            reportError(ErrorPost.reportError(postNum, e.toString()));
            resolveError(postNum, errorCheck().getErrorNum()); // 추가적인 복구가 필요하지는 않으므로 해결 처리.
            log.error("이미 저장된 게시글로 저장되지 않음 -> {} {} \n", postNum, title, e);
        } // 해당 페이지 내 마지막 글을 데이터베이스에 저장하던 도중 새로운 글이 작성되어 다음 페이지 첫번째 글로 넘어가면서 중복 오류가 발생한 건에 대해서만 예외 처리.
    }



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

    public void savePostItemNAVER(NaverPostItem postItem) {
        postRepository.newNaverPostItem(postItem);
    }
}
