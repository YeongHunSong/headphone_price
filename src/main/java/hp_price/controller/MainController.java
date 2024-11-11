package hp_price.controller;

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

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    @ResponseBody
    @RequestMapping("/")
    public String getHtml() {
        try {

            String url = "https://gall.dcinside.com/mgallery/board/lists/?id=newheadphone&sort_type=N&search_head=120&page=1";
            Connection connection = Jsoup.connect(url);
            Document doc = connection.get(); // post()
//            Elements el = doc.select("section div ul li");
            Elements el = doc.select(".gall_list");

            System.out.println(el);



            return el.toString();
        }catch(IOException e) {
            e.printStackTrace();
            return "1";
        }

    }

}
