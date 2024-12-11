package hpPrice.controller;

import hpPrice.paging.PageControl;
import hpPrice.paging.PageDto;
import hpPrice.search.SearchCond;
import hpPrice.service.CrawlingService;
import hpPrice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.SQLException;

import static java.lang.Thread.sleep;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;

    @GetMapping("/dcheadphone")
    public String dcHeadphone(Model model, @ModelAttribute(name = "pageDto") PageDto pageDto, @ModelAttribute(name = "cond")SearchCond cond) {

        model.addAttribute("pageControl", PageControl.create(pageDto, postService.postCount(cond)));
        model.addAttribute("postList", postService.findAll(pageDto, cond));

        return "dc/dcHeadphonePrice";
    }

    @GetMapping("/dcheadphone/{postNum}")
    public String dcHeadphoneDetail(@PathVariable Long postNum, Model model) {
        // TODO 글 내용 집어넣기




        return "dc/postDetail";
    }



    @ResponseBody
    @GetMapping("/test")
    public String test() {
            String res = "";

            try { // lastPageSearch
                String url = "https://gall.dcinside.com/mgallery/board/lists/?id=newheadphone&search_head=120";
                Connection connection = Jsoup.connect(url); // TODO user-agent 추가
                Document doc = connection.get(); // post()

                String pageUrl = doc.select(".page_end").attr("href");
                if (pageUrl.isEmpty()) { // 파싱 태그값이 변경된 경우, URL 이 변경된 경우
                    throw new SQLException();
                }
                int lastPage = Integer.parseInt(pageUrl.substring(pageUrl.indexOf("page=") + 5, pageUrl.indexOf("&search")));
                Long lastPostNum = postService.lastPostNum(); // PostService 에서 null 발생 시 0으로 반환 (DB에 저장값이 없는 경우)


                // TODO TEST 필요한 게시글 : 1177280 / 1177886 - 1178263 / 1176410
                // TODO 태그 없는 텍스트의 경우 날아가는 문제 해결하기 - 1176410
                // TODO URL 링크 관련도 삭제 고려
                pageLoop: // crawlingAndParsing
                    sleep(200);

                    Elements elements = Jsoup.connect(url + "&page=").get().select(".gall_list .us-post");
                    int i = 0;
//                    for (Element el : elements) {
                        sleep(500);
                        Elements els = Jsoup.connect(CrawlingService.POST_URL + "1176410").get()
                                .select(".write_div > *");
//                el.selectFirst(".gall_num").text()
                        els.removeIf(element ->
                                element.children().is("img") || element.children().is("iframe"));
                        i++;
                        System.out.println(i + "번째 게시글");
                        System.out.println(els);
//                    }



//                System.out.println(els.html());


//                System.out.println(els.comments());
//                System.out.println(els.dataNodes());
//                System.out.println(els.eachText());
//                System.out.println(els.hasText());

//
//                els.removeIf(element ->
//                        element.children().is("img"));
//
//
//                System.out.println(els);
//                res = els.html();


                /*for (Element el : els) {
//                    System.out.println(el.cssSelector());


//                    el.childNodeSize(); // 보유한 자식의 수
//                    el.lastChild(); // 마지막이면 null, 마지막이 아니면 child 반환
                    if (el.children().is("img")) {
                        continue;
                    }*/



//                    System.out.println(el);


//                    System.out.println(el.tagName());
//                    System.out.println(el.html());

//                    System.out.println(el.tagName());
//                    System.out.println(el.className());
//
//
//                    System.out.println(el.unwrap());
////                    System.out.println(el.unwrap());
//
////                    System.out.println(el.html());
//
//                    i++;
//
//                    System.out.println(i);




//                    System.out.println(el.text());


//                    System.out.println(el);
//                }





            } catch (SQLException e) {
                log.error("데이터값 변경 필요 -> ", e);
            } catch(IOException e) {
                log.error("IOException -> ", e);
            } catch(NullPointerException e) {
                log.error("NullPointerException -> ", e);
            } catch (Exception e) {
                log.error("sleep 실패 -> ", e);
            }
        return res;
    }
    


}
