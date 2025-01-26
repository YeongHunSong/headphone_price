package hpPrice.service.crawlingAndParsing;

import hpPrice.common.naverCafe.CategoryType;
import hpPrice.common.dateTime.DateTimeUtils;
import hpPrice.domain.NaverPostItem;
import hpPrice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;

import static hpPrice.common.CommonConst.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class NvCafeCrawlingService {
    private final PostRepository postRepository;
    private final NvLoginService nvLoginService;


    private static long postNum = 0;



//    @Scheduled(fixedDelay = 90 * 1000) // return 은 void / 매개 변수 받을 수 없음.
    public void naverCafePostItemCrawling() {
        log.info("NAVER CAFE 크롤링 시작 [{}]", DateTimeUtils.getCurrentDateTime());
        try {
            for (int category : CategoryType.getCategoryNumbers()) {
                long latestPostNum = latestPostNum(category);
                parsingLogic:
                for (int page = 1; page <= 10; page++) {
                    log.info("현재 페이지 -> [{}]page : 현재 카테고리 -> {}", page, CategoryType.getName(category));
                    // ### POST_ITEM 파싱 ###
                    Elements naverPostList = connectAndParsing(NV_POST_LIST_URL + NV_TAB_QUERY + category + NV_PAGE_QUERY + page, "div.article-board > table > tbody > tr");
                    naverPostList.removeIf(postItem -> postItem.hasClass("board-notice")); // 공지글 제거

                    for (Element postItem : naverPostList) {
                        postNum = Long.parseLong(postItem.selectFirst(".inner_number").text());
                        if (latestPostNum >= postNum) break parsingLogic;
                        // DB 마지막 저장값과 파싱값이 동일. (일반적인 경우) || 에러 복구 모드가 아닌데, DB 마지막 저장값보다 파싱값이 작음. (마지막 저장값에 해당하는 게시글이 삭제된 경우)

                        ChromeDriver driver = nvLoginService.getChromeDriver();
                        Set<Cookie> naverLoginCookies = nvLoginService.getNaverLoginCookie();

                        nvLoginService.driverGetAndWait(driver, NV_CAFE_URL);
                        for (Cookie cookie : naverLoginCookies) driver.manage().addCookie(cookie);

                        nvLoginService.driverGetAndWait(driver, NV_POST_URL + postNum);
                        // TODO 여기서 로그인이 제대로 되어있는지 확인하고, 로그인이 되어있지 않으면 쿠키 리프레쉬

                        // 파싱 코드 ~~



                        savePostItem(postItem, category); // TODO 저장 통합

                        // ### POST 파싱 ###
//                        Elements post = connectAndParsing(DC_POST_URL + DC_POST_NUM_QUERY + postNum, ".write_div > *");
//                        post.removeIf(postLine -> postLine.select("iframe").is("iframe")); // 동영상 링크 삭제
//                        for (Element postLine : post) imageUrlCheck(postLine);

                    }
                }
            }


            // TODO 셀레니움을 이용한 게시글 크롤링의 경우에는, 그냥 postNum 으로 넣어서 크롤링을 하자.
            // TODO post 의 경우 로그인 쿠키 관련해서 에러 확인이 필요한듯.
            // error report 할 때 카테고리도 같이 넘겨주기

        } catch (IOException e) { // 타임아웃, 데이터 없음, 500에러(HttpStatusException) 등등
//            reportError(ErrorPost.reportError(postNum, e.toString()));
            log.error("크롤링 사이트 연결 관련 Exception -> ", e);
        } catch (Exception e) {
            log.error("Exception 발생 -> ", e);
        }
        log.info("NAVER CAFE 크롤링 종료 [{}]", DateTimeUtils.getCurrentDateTime());
    }

    private void savePostItem(Element postItem, int category) {
//        long postNum = Long.parseLong(postItem.selectFirst(".inner_number").text());
        String title = postItem.selectFirst(".article").text();
        try {
            savePostItemNAVER(
                    NaverPostItem.newPostItem(
                            postNum,
                            title,
                            category,
                            "https://cafe.naver.com/drhp/" + postNum,
                            postItem.selectFirst(".m-tcol-c").text(),
                            postItem.selectFirst(".mem-level").html(),
                            DateTimeUtils.parseNaverDateTime(postItem.selectFirst(".td_date").text())));
        } catch (DuplicateKeyException e) {
            log.error("이미 저장된 게시글로 저장되지 않음 -> {} {} {} \n", postNum, title, CategoryType.getName(category), e);
        } // 해당 페이지 내 마지막 글을 데이터베이스에 저장하던 도중 새로운 글이 작성되어 다음 페이지 첫번째 글로 넘어가면서 중복 오류가 발생한 건에 대해서만 예외 처리.
    }



    public Elements connectAndParsing(String connectUrl, String selectQuery) throws IOException, InterruptedException {
        for (int tryCount = 0; tryCount < MAX_RETRY_COUNT; tryCount++) {
            try {
                Thread.sleep(SLEEP_TIME + 300);
                Elements parsingData = Jsoup.connect(connectUrl)
                        .userAgent(USER_AGENT)
                        .timeout(TIME_OUT)
                        .get()
                        .select(selectQuery);
                if (parsingData.isEmpty()) throw new IOException("화이트 페이지 에러 발생 " + connectUrl);
                return parsingData;
            } catch (IOException e) {
                if (tryCount == MAX_RETRY_COUNT - 1) throw e;
                if (tryCount >= 2) {
                    log.info("에러 발생으로 인한 재시도 횟수 => {}회", tryCount + 1);
                    log.info("에러 발생한 URL = {}", connectUrl);
                }
                Thread.sleep(SLEEP_TIME * (tryCount + 1));
            }
        }
        throw new IOException("페이지 로드 실패");
    }

    public void savePostItemNAVER(NaverPostItem postItem) {
        postRepository.newNaverPostItem(postItem);
        log.info("저장한 게시글 - {} {}", postItem.getPostNum(), postItem.getTitle());
    }

    public Long latestPostNum(int category) {
        // latestPostNum 이 null 인 경우(=DB에 아무 값이 없음), 0으로 반환
        Long latestPostNum = postRepository.findLatestNaverPostNum(category);
        return latestPostNum == null ? 0 : latestPostNum;
    }
}
