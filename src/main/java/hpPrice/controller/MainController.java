package hpPrice.controller;

import hpPrice.common.naverCafe.CategoryType;
import hpPrice.domain.common.Post;
import hpPrice.domain.dc.PostItem;
import hpPrice.common.paging.PageControl;
import hpPrice.common.paging.PageDto;
import hpPrice.domain.common.SearchCond;
import hpPrice.domain.naver.NaverPostItem;
import hpPrice.service.PostService;
import hpPrice.service.crawlingAndParsing.NvLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.util.Set;

import static hpPrice.common.CommonConst.*;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;
    private final NvLoginService nvLoginService;

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
    public String dcSffPostList(Model model, @ModelAttribute(name = "pageDto") PageDto pageDto, @ModelAttribute(name = "cond") SearchCond cond) {
        model.addAttribute("pageControl", PageControl.createPage(pageDto, postService.countPostItems(cond)));
        model.addAttribute("postItem", postService.findPostItems(pageDto, cond));
        return "dc/home";

        // TODO 저장할 때부터 모든 내용을 소문자로 DB에 넣는다거나?
        // 시스템 설정 활용: MySQL 의 경우, lower_case_table_names 시스템 변수를 1로 설정하여 대소문자를 구분하지 않도록 할 수 있습니다.
        // https://oneny.tistory.com/106
    }

    @GetMapping("/dcsff/{postNum}")
    public String dcsffPostDetail(Model model, @PathVariable("postNum") Long postNum) {
        PostItem postItem = postService.findPostItem(postNum);
        if (postItem == null) return "redirect:/dcsff";
        Post post = postService.findPost(postNum);

        model.addAttribute("postItem", postItem);
        model.addAttribute("post", post);
        return "dc/postDetail";
    }


    // NAVER CAFE

    @ResponseBody
    @GetMapping("/drhp")
    public String drhpHome() {

        return "bad";
    }


    @GetMapping("/drhp/{category}") // String 으로 바꿀까?
    public String drhpPostList(@ModelAttribute(name = "pageDto") PageDto pageDto, @ModelAttribute(name = "cond") SearchCond cond,
                               @PathVariable("category") int category, Model model) {
        if (!CategoryType.isCategory(category)) return "redirect:/drhp";

        pageDto.setPageView(15);// 네이버 카페 기본값 15
        model.addAttribute("pageControl", PageControl.createPage(pageDto, postService.countNvPostItems(cond, category)));
        model.addAttribute("postItem", postService.findNaverPostItems(pageDto, cond, category));
        return "naverCafe/home";
    }

    @GetMapping("/drhp/{postNum}")
    public String drhpPostDetail (Model model, @PathVariable("postNum") Long postNum) {
        NaverPostItem postItem = postService.findNaverPostItem(postNum);
        if (postItem == null) return "redirect:/drhp";
        Post post = postService.findNaverPost(postNum);

        model.addAttribute("postItem", postItem);
        model.addAttribute("post", post);
        return "naverCafe/postDetail";
    }






    // 닥터헤드폰 회원 URL: https://cafe.naver.com/ca-fe/cafes/11196414/members/gWPPtZhVptCHRmQPcBfqAw
    // 글 상세에서는 작성일자 전체 표시



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
//    @GetMapping("/test")
    public String selenium() throws Exception {
        ChromeDriver driver = nvLoginService.getChromeDriver();
        Set<Cookie> naverLoginCookies = nvLoginService.getNaverLoginCookie();

            try {
                nvLoginService.getDriverAndWait(driver, NV_CAFE_URL);
                for (Cookie cookie : naverLoginCookies) driver.manage().addCookie(cookie);
                nvLoginService.getDriverAndWait(driver, NV_POST_URL + 1693616);

                WebElement seleniumEle = driver.findElement(By.className("ArticleContentBox"));
                Elements nvCafePost = Jsoup.parse(seleniumEle.getAttribute("outerHTML")).select(".ArticleContentBox > div");

                String nickLevel = nvCafePost.select(".nick_level").text();
                String wDate = nvCafePost.select(".date").text();


                Elements post = nvCafePost.select(".se-module-text > p");
                String price = post.select("*:matchesOwn(" + PRICE_PATTERN.pattern() + ")").text()
                        .replaceAll(".*:\\s*", "").trim();

                post.removeIf(postLine -> REMOVE_PATTERN.matcher(postLine.text()).find());



//                if (post.get(15).text().trim().equals("@ 아래 양식 반드시 적어서 게시하세요.")) { // 글 양식 그대로 작성한 경우
//                    post.subList(0, 16).clear();
//                } else { // 글 양식을 건드린 경우, 글보다 사진이 먼저 올라와있는 경우
//                }




//                Elements images = nvCafePost.select(".se-module-image > a > img"); // 이미지
                // 네이버 카페의 이미지는 시도해본 결과, 외부에서 불러올 수 없게 되어있는 듯함.

                // https://stackoverflow.com/questions/8706548/disable-direct-access-to-images
                // 이미지 자체를 Base64 로 인코딩하여 html 에 직접 삽입하는 방법도 있기는 하다.
                // 하지만 이미지 업로드가 꼭 필요한 기능인지 에 대해서는 의문.


                return post.toString();
            } finally {
                driver.quit();
            }
    }
}


//// Jsoup 으로는 아무리 해도 네이버 카페 화면 크롤링 불가능.
//        Map<String, String> naverLoginCookies = naverLoginService.getCookieToJsoup();
//Document document = Jsoup.url("https://cafe.naver.com/drhp/2358449")
//                        .userAgent(USER_AGENT)
//                        .timeout(TIME_OUT)
//                        .cookies(naverLoginCookies)
//                        .get();
//                return document.toString();


