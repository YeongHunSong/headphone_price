package hp_price.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

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
            Elements els = doc.select(".gall_list .us-post");

//            System.out.println(els.get(0));

//            Element el = els.get(0);
//            Attributes attributes = el.attributes();
//
//            for (Attribute a : attributes) {
//                System.out.println(a);
//            }

            List<Element> list = new Elements();
            list.addAll(els);
            Element ele = list.get(0);


            System.out.println("textValue = " + ele.text());



            System.out.println("postNum = " + ele.selectFirst(".gall_num").text());
            System.out.println("title = " + ele.selectFirst(".gall_tit").selectFirst("[href]").text());
            System.out.println("url = " + ele.selectFirst(".gall_tit").selectFirst("[href]").attributes());
            System.out.println("date  = " + ele.selectFirst(".gall_date").attribute("title").getValue());
            System.out.println("nickname  = " + ele.selectFirst(".nickname").selectFirst("em").text());
            System.out.println("userId  = " + ele.selectFirst(".gall_writer").attribute("data-uid").getValue());


//            System.out.println("Attributes = " + list.get(0).attributes());
            
            
//            System.out.println("tag = " + list.get(0).tag());
//            System.out.println("html = " + list.get(0).html());
//            System.out.println("className = " + list.get(0).className());
//            System.out.println("baseUri = " + list.get(0).baseUri());
//            System.out.println("cssSelector = " + list.get(0).cssSelector());
//            System.out.println("data = " + list.get(0).data());
//            System.out.println("dataset = " + list.get(0).dataset());
//            System.out.println("id = " + list.get(0).id());
//            System.out.println("ownText = " + list.get(0).ownText());
//            System.out.println("nodeName = " + list.get(0).nodeName());
//            System.out.println("normalName = " + list.get(0).normalName());
//            System.out.println("getAllElements = " + list.get(0).getAllElements());



//            System.out.println(list.get(0).toString());
            return list.get(0).toString();
//            return els.toString();
        }catch(IOException e) {
            System.out.println("실패 : " + e);
            return "1";
        }

    }

}
