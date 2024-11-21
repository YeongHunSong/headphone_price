package hpPrice.controller;

import hpPrice.domain.Post;
import hpPrice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//    sleep(1000);
    @ResponseBody
    @RequestMapping("/")
    public String dcHeadphone() {
        try {
            // &page=1
            String url = "https://gall.dcinside.com/mgallery/board/lists/?id=newheadphone&search_head=120";
            Connection connection = Jsoup.connect(url);
            Document doc = connection.get(); // post()

            String pageUrl = doc.select(".page_end").attr("href");
            int lastPage = Integer.parseInt(pageUrl.substring(pageUrl.indexOf("page=")+5, pageUrl.indexOf("&search")));
//            int lastPage = Integer.parseInt(pageUrl.split("page=")[1].split("&search")[0]);


            // 1Page 내용만 긁어오기
            Elements elements = doc.select(".gall_list .us-post");
            for (Element el : elements) {
                if (postService.isCheckDup(Long.valueOf(el.selectFirst(".gall_num").text()))) {
                    continue; // 이미 저장한 게시글의 경우 넘어감
                }

                postService.newPostDC(
                        Post.newPost(Long.valueOf(el.selectFirst(".gall_num").text()),
                                el.selectFirst(".gall_tit [href]").text(),
                                el.selectFirst(".gall_tit a").absUrl("href"),
                                el.selectFirst(".nickname em").text(),
                                el.selectFirst(".gall_writer").attr("data-uid"),
                                LocalDateTime.parse(el.selectFirst(".gall_date").attr("title"), DATE_FORMATTER)));
            }
            sleep(500); // 혹시나 해서

            // 2Page ~ 내용도 긁어오기





//            Element ele = elements.get(0);
//            System.out.println(LocalDateTime.parse("2024-11-19 17:08:43", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//            System.out.println("postNum = " + ele.selectFirst(".gall_num").text());
//            System.out.println("title = " + ele.selectFirst(".gall_tit [href]").text());
//            System.out.println("url = " + ele.selectFirst(".gall_tit a").baseUri());
//            System.out.println("userId  = " + ele.selectFirst(".gall_writer").attr("data-uid"));
//            System.out.println("nickname  = " + ele.selectFirst(".nickname em").text());
//            System.out.println("date  = " + LocalDateTime.parse(ele.selectFirst(".gall_date").attr("title"), DATE_FORMATTER));


            return elements.get(0).toString();
        }catch(IOException e) {
            log.error("IOException = ", e);
            return "1";
        } catch (Exception e) {
            log.error("sleep 실패 = ", e);
            return "1";
        }

    }

}
