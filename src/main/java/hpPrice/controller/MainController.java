package hpPrice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import hpPrice.common.dateTime.DateTimeUtils;
import hpPrice.common.naverCafe.CategoryType;
import hpPrice.domain.NaverPostItem;
import hpPrice.domain.Post;
import hpPrice.domain.PostItem;
import hpPrice.common.paging.PageControl;
import hpPrice.common.paging.PageDto;
import hpPrice.domain.SearchCond;
import hpPrice.service.crawlingAndParsing.DcGallCrawlingService;
import hpPrice.service.PostService;
import hpPrice.service.crawlingAndParsing.NaverLoginCookieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

import static hpPrice.common.CommonConst.*;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;
    private final NaverLoginCookieService naverLoginService;
    private final DcGallCrawlingService DcGallCrawlingService;

    @InitBinder
    public void initBinder(WebDataBinder binder) { // 숫자로 변환되지 않는 값이 바인딩 되었을 때 방지
        binder.setDisallowedFields("pageView");

        binder.registerCustomEditor(int.class, "page", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    setValue(Integer.parseInt(text));
                } catch (NumberFormatException e) {
                    setValue(1);
                } // @RequestParam 에는 적용이 안 되는듯?
            }
        });

        binder.registerCustomEditor(long.class, "postNum", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    setValue(Long.parseLong(text));
                } catch (NumberFormatException e) {
                    setValue(0);
                }
            }
        });

        binder.registerCustomEditor(int.class, "category", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    setValue(Long.parseLong(text));
                } catch (NumberFormatException e) {
                    setValue(0);
                }
            }
        });

//        특정 필드 바인딩 제어:
//          setDisallowedFields(): 특정 필드의 바인딩을 금지합니다.
//          setAllowedFields(): 허용할 필드만 지정하여 바인딩합니다.
//        커스텀 포맷터 및 에디터 등록:
//          addCustomFormatter(): 사용자 정의 Formatter 를 등록합니다.
//          registerCustomEditor(): 사용자 정의 PropertyEditor 를 등록합니다.
//        유효성 검사기 추가:
//          addValidators(): 특정 Validator 를 등록하여 객체의 유효성을 검사합니다.
//        데이터 형 변환:
//          요청으로 들어온 문자열 데이터를 객체의 속성 타입에 맞게 변환합니다.
//        특정 객체에만 바인딩 적용:
//          @InitBinder("objectName")을 사용하여 특정 객체에만 바인딩 또는 검증 설정을 적용할 수 있습니다.

    }

    // TODO DC 페이지나, 닥헤 게시판 고를 수 있는 홈화면 만들기

    // DC

    @GetMapping("/dcsff")
    public String dcSff(Model model, @ModelAttribute(name = "pageDto") PageDto pageDto, @ModelAttribute(name = "cond") SearchCond cond) {
        model.addAttribute("pageControl", PageControl.createPage(pageDto, postService.countPostItems(cond)));
        model.addAttribute("postItem", postService.findPostItems(pageDto, cond));
        return "dc/home";

        // TODO 저장할 때부터 모든 내용을 소문자로 DB에 넣는다거나?
        // 시스템 설정 활용: MySQL의 경우, lower_case_table_names 시스템 변수를 1로 설정하여 대소문자를 구분하지 않도록 할 수 있습니다.
        // https://oneny.tistory.com/106
    }

    @GetMapping("/dcsff/{postNum}")
    public String dcHeadphoneDetail(Model model, @PathVariable("postNum") Long postNum) {
        PostItem postItem = postService.findPostItem(postNum);
        if (postItem == null) return "redirect:/dcsff";
        Post post = postService.findPost(postNum);

        model.addAttribute("postItem", postItem);
        model.addAttribute("post", post);
        return "dc/postDetail";
    }


    // NAVER CAFE

    @GetMapping("/drhp/{category}")
    public String drhp(@ModelAttribute(name = "pageDto") PageDto pageDto, @ModelAttribute(name = "cond") SearchCond cond,
                       @PathVariable("category") int category, Model model) {
        if (!CategoryType.isCategory(category)) {
            return "redirect:/dcsff";
        }


        pageDto.setPageView(15);// 네이버 카페 기본값 15
        model.addAttribute("pageControl", PageControl.createPage(pageDto, postService.countNvPostItems(cond, category)));
        model.addAttribute("postItem", postService.findNvPostItems(pageDto, cond, category));
        return "naverCafe/home";
    }








    // 닥터헤드폰 회원 URL: https://cafe.naver.com/ca-fe/cafes/11196414/members/gWPPtZhVptCHRmQPcBfqAw
    // 글 상세에서는 작성일자 전체 표시


    @ResponseBody
    @GetMapping("/test")
    public String nvDRHPHeadphone() throws IOException, InterruptedException {
        Elements postList = DcGallCrawlingService.connectAndParsing("https://cafe.naver.com/ArticleList.nhn?search.clubid=11196414&search.menuid=21&search.page=1",
                "div.article-board > table > tbody > tr");
        postList.removeIf(postItem -> postItem.hasClass("board-notice")); // 공지글 제거

        // https://cafe.naver.com/drhp/2354201

       for (Element postItem : postList) {
           long postNum = Long.parseLong(postItem.selectFirst(".inner_number").text());
           System.out.println(NaverPostItem.newPostItem(
                   postNum,
                   postItem.selectFirst(".article").text(),
                   21,
                   postItem.selectFirst(".article").absUrl("href"),
                   postItem.selectFirst(".m-tcol-c").text(),
                   postItem.selectFirst(".mem-level").html(),
                   DateTimeUtils.parseNaverDateTime(postItem.selectFirst(".td_date").text())
           ));
           // postItem / post 저장

       }



        return postList.toString();
    }


    @ResponseBody
    @GetMapping("/cookieRefresh")
    public String cookieRefresh() throws JsonProcessingException { // TODO 쿠키가 만료되었을 경우, 자동으로 쿠키를 새로 발급받은 후 크롤링을 진행할 수 있도록.
        naverLoginService.setNaverLoginCookies();
        return naverLoginService.getNaverLoginCookies().toString();
    }



    // https://cafe.naver.com/ArticleRead.nhn?articleid=2355357&where=search&clubid=11196414&tc=naver_search
    // https://cafe.naver.com/ca-fe/cafes/11196414/articles/2355357?where=search&tc=naver_search&oldPath=%2FArticleRead.nhn%3Farticleid%3D2355357%26where%3Dsearch%26clubid%3D11196414%26tc%3Dnaver_search
    // https://cafe.naver.com/ca-fe/cafes/11196414/articles/2355357?oldPath=%2FArticleRead.nhn%3Farticleid%3D2355357

    // https://cafe.naver.com/drhp/2355357
    // https://m.cafe.naver.com/ca-fe/web/cafes/11196414/
    // https://cafe.naver.com/ArticleRead.nhn?clubid=11196414&page=1&menuid=21&boardtype=L&articleid=2355357&referrerAllArticles=false



    // https://cafe.naver.com/f-e/cafes/11196414/articles/2356030?menuid=21&referrerAllArticles=false
    // https://cafe.naver.com/ca-fe/cafes/11196414/articles/2356030?referrerAllArticles=false // 이게 핵심이다


    // https://cafe.naver.com/drhp?iframe_url=/ArticleList.nhn%3Fsearch.clubid=11196414%26search.menuid=21%26search.boardtype=L
    // https://cafe.naver.com/ArticleList.nhn?search.clubid=11196414&search.menuid=21

    // https://cafe.naver.com/drhp?iframe_url=%2FArticleRead.nhn%3Fclubid%3D11196414%26articleid%3D2356022%26where%3Dsearch%26tc%3Dnaver_search
    // https://cafe.naver.com/drhp?iframe_url=/ArticleRead.nhn?articleid=2356030&where=search&clubid=11196414
    // https://cafe.naver.com/ArticleRead.nhn?articleid=2356030&clubid=11196414

    // https://cafe.naver.com/ca-fe/cafes/11196414/articles/2356030?where=search&tc=naver_search&oldPath=%2FArticleRead.nhn%3Farticleid%3D2356030%26where%3Dsearch%26clubid%3D11196414%26tc%3Dnaver_search


    @ResponseBody
    @GetMapping("/sele")
    public String selenium() throws Exception {
        Map<String, String> naverLoginCookies = naverLoginService.getNaverLoginCookies();



        ChromeOptions options = new ChromeOptions()
                .addArguments("--remote-allow-origins=*")
                .addArguments("start-maximized")
                .addArguments("user-agent=" + USER_AGENT);

        ChromeDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));

//        Set<Cookie> cookies = naverLoginService.setNaverLoginCookies();
//
//        for (Cookie cookie : cookies) {
//            driver.manage().addCookie(cookie);
//        }

        driver.manage().deleteAllCookies();



        System.out.println("addCookie 완료");


        try {
            driver.get("https://www.naver.com");
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));
            for (Map.Entry<String, String> entry : naverLoginCookies.entrySet()) {
                driver.manage().addCookie(new Cookie(entry.getKey(), entry.getValue()));
            }

            Thread.sleep(5000);

            driver.get("https://www.naver.com");

            Thread.sleep(5000);

            driver.get("https://cafe.naver.com/ca-fe/cafes/11196414/articles/2357666?referrerAllArticles=false");
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));



//            driver.navigate().refresh(); // 쿠키 적용이 제대로 안 된 경우 새로고침해라.
//            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));
            Thread.sleep(1000000000);

        } finally {
            driver.quit();
        }


//        for (int tryCount = 0; tryCount < MAX_RETRY_COUNT; tryCount++) {
//            try {
//                Thread.sleep(SLEEP_TIME);
//
//
//
//
//                Document document = Jsoup.connect("")
//                        .cookies(naverLoginCookies)
//                        .userAgent(USER_AGENT)
//                        .timeout(TIME_OUT)
//                        .referrer("https://search.naver.com/")
//                        .get();
//
//                return document.toString();
//            } catch (IOException e) {
//                if (tryCount == MAX_RETRY_COUNT - 1) throw e;
//                log.info("에러 발생으로 인한 재시도 횟수 => {}회", tryCount + 1);
//                Thread.sleep(SLEEP_TIME * (tryCount + 1));
//            }
//        }
//        throw new IOException("페이지 로드 실패");
        return naverLoginCookies.toString();
    }
}


