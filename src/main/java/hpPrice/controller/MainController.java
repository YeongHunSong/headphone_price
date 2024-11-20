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
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @ResponseBody
    @RequestMapping("/")
    public String dcHeadphone() {
        try {

            // TODO 페이지 바꾸는 기능 추가
            String url = "https://gall.dcinside.com/mgallery/board/lists/?id=newheadphone&sort_type=N&search_head=120&page=1";
            Connection connection = Jsoup.connect(url);
            Document doc = connection.get(); // post()
            Elements els = doc.select(".gall_list .us-post");

            List<Element> list = new Elements();
            list.addAll(els);


            for (Element el : list) {
//                if (postService.isCheckDup(Long.valueOf(el.selectFirst(".gall_num").text()))) {
//                    continue; // 이미 저장한 게시글의 경우 넘어감
//                }

                    postService.newPostDC(
                            Post.newPost(Long.valueOf(el.selectFirst(".gall_num").text()),
                            el.selectFirst(".gall_tit").selectFirst("[href]").text(),
                            el.selectFirst(".gall_tit").selectFirst("a").attribute("href").getValue(),
                            el.selectFirst(".nickname").selectFirst("em").text(),
                            el.selectFirst(".gall_writer").attribute("data-uid").getValue(),
                            LocalDateTime.parse(el.selectFirst(".gall_date").attribute("title").getValue(), DATE_FORMATTER)));
            }



//            Element ele = list.get(0);
//            System.out.println(LocalDateTime.parse("2024-11-19 17:08:43", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//            System.out.println("postNum = " + ele.selectFirst(".gall_num").text());
//            System.out.println("title = " + ele.selectFirst(".gall_tit").selectFirst("[href]").text());
//            System.out.println("url = " + ele.selectFirst(".gall_tit").selectFirst("a").attribute("href").getValue());
//            System.out.println("userId  = " + ele.selectFirst(".gall_writer").attribute("data-uid").getValue());
//            System.out.println("nickname  = " + ele.selectFirst(".nickname").selectFirst("em").text());
//            System.out.println("date  = " + ele.selectFirst(".gall_date").attribute("title").getValue());


            return list.toString();
        }catch(IOException e) {
            System.out.println("실패 : " + e);
            return "1";
        }

    }

}
