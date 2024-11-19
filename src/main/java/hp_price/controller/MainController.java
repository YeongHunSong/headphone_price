package hp_price.controller;

import hp_price.domain.Post;
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

    @ResponseBody
    @RequestMapping("/")
    public String dcHeadphone() {
        try {

            String url = "https://gall.dcinside.com/mgallery/board/lists/?id=newheadphone&sort_type=N&search_head=120&page=1";
            Connection connection = Jsoup.connect(url);
            Document doc = connection.get(); // post()
            Elements els = doc.select(".gall_list .us-post");

            List<Element> list = new Elements();
            list.addAll(els);
            Element ele = list.get(0);


            ;

            System.out.println(LocalDateTime.parse("2024-11-19 17:08:43", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//            LocalDateTime date = LocalDateTime.parse("2024-11-19 17:08:43");
//            System.out.println("parseDate = " + date);

//            Post.newPost(Long.valueOf(ele.selectFirst(".gall_num").text()),
//                    ele.selectFirst(".gall_tit").selectFirst("[href]").text(),
//                    ele.selectFirst(".gall_tit").selectFirst("a").attribute("href").getValue(),
//                    ele.selectFirst(".gall_date").attribute("title").getValue(),
//                    ele.selectFirst(".nickname").selectFirst("em").text(),
//                    LocalDateTime.parse(ele.selectFirst(".gall_writer").attribute("data-uid").getValue())
//                    );


            System.out.println("postNum = " + ele.selectFirst(".gall_num").text());
            System.out.println("title = " + ele.selectFirst(".gall_tit").selectFirst("[href]").text());
            System.out.println("url = " + ele.selectFirst(".gall_tit").selectFirst("a").attribute("href").getValue());
            System.out.println("date  = " + ele.selectFirst(".gall_date").attribute("title").getValue());
            System.out.println("nickname  = " + ele.selectFirst(".nickname").selectFirst("em").text());
            System.out.println("userId  = " + ele.selectFirst(".gall_writer").attribute("data-uid").getValue());


            return list.get(0).toString();
        }catch(IOException e) {
            System.out.println("실패 : " + e);
            return "1";
        }

    }

}
