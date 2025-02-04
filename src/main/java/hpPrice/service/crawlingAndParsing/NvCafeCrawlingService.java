package hpPrice.service.crawlingAndParsing;

import hpPrice.common.naverCafe.CategoryType;
import hpPrice.common.dateTime.DateTimeUtils;
import hpPrice.domain.common.Post;
import hpPrice.domain.naver.NaverPostItem;
import hpPrice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;

import static hpPrice.common.CommonConst.*;
import static java.lang.Thread.sleep;


@Slf4j
@Service
@RequiredArgsConstructor
public class NvCafeCrawlingService {
    private final PostRepository postRepository;
    private final SeleniumNvLoginService seleniumNvLoginService;
    private final JsoupDcCrawlingService jsoupDcCrawlingService;

    @Scheduled(fixedDelay = 90 * 1000) // return 은 void / 매개 변수 받을 수 없음.
    public void naverCafePostCrawling() {
        log.info("NAVER CAFE 크롤링 시작 [{}]", DateTimeUtils.getCurrentDateTime());
        ChromeDriver driver = naverLoginCookieReady();
        try {
            for (int category : CategoryType.getCategoryNumbers()) {
                long latestPostNum = latestPostNum(category);
                parsingLogic:
                for (int page = 1; page <= 3; page++) {
                    log.info("현재 페이지 -> [{}]page : 현재 카테고리 -> {}", page, CategoryType.getName(category));
                    /// ### POST_ITEM Parsing ###
                    Elements naverPostList = jsoupDcCrawlingService.jsoupConnectAndParsing(
                            NV_POST_LIST_URL + category + NV_PAGE_QUERY + page,
                            "div.article-board > table > tbody > tr");
                    naverPostList.removeIf(postItem -> postItem.hasClass("board-notice")); // 공지글 제거

                    /// ### POST Parsing ###
                    for (Element postItem : naverPostList) {
                        long postNum = Long.parseLong((postItem.selectFirst(".inner_number")).text());
                        if (latestPostNum >= postNum) break parsingLogic;
                        // '=' DB 마지막 저장값과 파싱값이 동일. (일반적인 경우)
                        // '>' DB 마지막 저장값보다 파싱값이 작음. (마지막 저장값에 해당하는 게시글이 삭제된 경우)

                        Elements naverCafePost = getNaverCafePost(driver, postNum);

                        /// ### POST_ITEM & POST Save ###
                        saveNaverPostAndPostItem(postItem, category, naverCafePost);
                    }
                }
            }
            // TODO 추후 errorReport 관련 추가하기 + 카테고리 값도 같이 넘겨주기

        } catch (IOException e) { // 타임아웃, 데이터 없음, 500에러(HttpStatusException) 등등
            log.error("크롤링 사이트 연결 관련 Exception -> ", e);
        } catch (Exception e) {
            log.error("Exception 발생 -> ", e);
        } finally {
            driver.quit();
            log.info("NAVER CAFE 크롤링 종료 [{}]", DateTimeUtils.getCurrentDateTime());
        }
    }

    private Elements getNaverCafePost(ChromeDriver driver, long postNum) {
        for (int tryCount = 0; tryCount < MAX_RETRY_COUNT; tryCount++) {
            try {
                seleniumNvLoginService.getDriverAndWait(driver, NV_POST_URL + postNum);
                return Jsoup.parse(
                                driver.findElement(By.className("ArticleContentBox")).getAttribute("outerHTML"))
                        .select(".ArticleContentBox > div");
            } catch (NoSuchElementException e) { // findElement 를 할 때 해당 값이 없으면 발생.
                log.error("NoSuchElementException -> ", e);
            }
        }
        driver.quit();
        throw new RuntimeException("페이지 로드 실패");
    }

    private ChromeDriver naverLoginCookieReady() {
        ChromeDriver driver = seleniumNvLoginService.getChromeDriver();
        for (int tryCount = 0; tryCount < MAX_RETRY_COUNT; tryCount++) {
            try {
                Set<Cookie> naverLoginCookies = seleniumNvLoginService.getNaverLoginCookie();
                seleniumNvLoginService.getDriverAndWait(driver, NV_CAFE_URL);
                for (Cookie cookie : naverLoginCookies) driver.manage().addCookie(cookie);

                seleniumNvLoginService.getDriverAndWait(driver, NV_POST_URL + 1710711);
                if (driver.findElements(By.className("ArticleContentBox")).isEmpty()) {
                    throw new IllegalArgumentException("Cookie Expired");
                }
                return driver;
            } catch (IllegalArgumentException | InvalidCookieDomainException e) { // 쿠키 없음 & 만료
                log.error("Cookie Expired -> ", e);
                seleniumNvLoginService.updateNaverLoginCookies();
            }
        }
        driver.quit();
        throw new RuntimeException("쿠키 갱신 실패");
    }

    private void saveNaverPostAndPostItem(Element postItem, int category, Elements naverCafePost) {
        long postNum = Long.parseLong(postItem.selectFirst(".inner_number").text());
        String title = postItem.selectFirst(".article").text();
        Elements postContent = naverCafePost.select(".se-module-text > p");
        postContent.removeIf(postLine -> REMOVE_PATTERN.matcher(postLine.text()).find());
        try {
            saveNaverPostItem(
                    NaverPostItem.newPostItem(
                            postNum,
                            title,
                            category,
                            "https://cafe.naver.com/drhp/" + postNum, // url
                            postItem.selectFirst(".m-tcol-c").text(), // nickname
                            postItem.selectFirst(".mem-level").html(), // memLevelIcon
                            naverCafePost.select(".nick_level").text(), // memLevel
                            DateTimeUtils.parseNaverDateTime(naverCafePost.select(".date").text()), // wDate
                            postContent.select("*:matchesOwn(" + PRICE_PATTERN.pattern() + ")").text() // price
                                    .replaceAll(".*:\\s*", "").trim()));
            saveNaverPost(
                    Post.newPost(postNum,
                            postContent.outerHtml()));
        } catch (DuplicateKeyException e) {
            log.error("이미 저장된 게시글로 저장되지 않음 -> {} {} {} \n", CategoryType.getName(category), postNum, title, e);
        } // 해당 페이지 내 마지막 글을 데이터베이스에 저장하던 도중 새로운 글이 작성되어 다음 페이지 첫번째 글로 넘어가면서 중복 오류가 발생한 건에 대해서만 예외 처리.
    }

    public Long latestPostNum(int category) {
        // latestPostNum 이 null 인 경우(=DB에 아무 값이 없음), 0으로 반환
        Long latestPostNum = postRepository.findLatestNaverPostNum(category);
        return latestPostNum == null ? 0 : latestPostNum;
    }

    public void saveNaverPostItem(NaverPostItem postItem) {
        postRepository.newNaverPostItem(postItem);
        log.info("저장한 게시글 - {} {}", postItem.getPostNum(), postItem.getTitle());
    }

    public void saveNaverPost(Post post) {
        postRepository.newNaverPost(post);
    }
}
