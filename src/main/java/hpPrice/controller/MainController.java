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


                pageLoop: // crawlingAndParsing
                    sleep(200);

                    Elements elements = Jsoup.connect(url + "&page=").get().select(".gall_list .us-post");
                    Element element1 = elements.get(0);
                    Elements els = Jsoup.connect(CrawlingService.POST_URL + element1.selectFirst(".gall_num").text()).get().select(".write_div");

                Elements img = els.not("img");
                for (Element el : img) {

//                    System.out.println(el.val());

//                    System.out.println(el.wholeText());
//                    System.out.println(el.ownText());

                    System.out.println(el);
                }

                    res = img.html();




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
