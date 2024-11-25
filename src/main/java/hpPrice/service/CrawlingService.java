package hpPrice.service;

import hpPrice.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Thread.sleep;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingService {

    private final PostService postService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(fixedDelay = 60000) // 60초 주기로 크롤링
    public void crawlingDC() {
        log.info("크롤링 동작 중 [{}]", LocalDateTime.now().format(DATE_FORMATTER));

        try {
            String url = "https://gall.dcinside.com/mgallery/board/lists/?id=newheadphone&search_head=120";
            Connection connection = Jsoup.connect(url); // TODO user-agent 추가
            Document doc = connection.get(); // post()

            String pageUrl = doc.select(".page_end").attr("href");
            int lastPage = Integer.parseInt(pageUrl.substring(pageUrl.indexOf("page=")+5, pageUrl.indexOf("&search")));
//            int lastPage = Integer.parseInt(pageUrl.split("page=")[1].split("&search")[0]);
            Long lastPostNum = postService.lastPostNum();


            pageLoop:
            for (int i=1; i<=lastPage; i++) {
                sleep(500);
                Elements elements = Jsoup.connect(url + "&page=" + i).get().select(".gall_list .us-post");
                log.info("현재 파싱 페이지 -> [{}]page", i);
                for (Element element : elements) {
                    if (Long.valueOf(element.selectFirst(".gall_num").text()).equals(lastPostNum)) {
                        log.info("DB 갱신 완료: break 페이지 -> [{}]page", i);
                        break pageLoop; // 필요한 게시글까지 다 받아온 경우
                    }
                    if (postService.isCheckDup(Long.valueOf(element.selectFirst(".gall_num").text()))) {
                        log.info("continue 페이지 -> [{}]page", i);
                        continue; // 이미 저장한 게시글의 경우 넘어감
                    }
                    parseDcPage(element);
                }
            }

//            Element ele = elements.get(0);
//            System.out.println(LocalDateTime.parse("2024-11-19 17:08:43", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//            System.out.println("postNum = " + ele.selectFirst(".gall_num").text());
//            System.out.println("title = " + ele.selectFirst(".gall_tit [href]").text());
//            System.out.println("url = " + ele.selectFirst(".gall_tit a").baseUri());
//            System.out.println("userId  = " + ele.selectFirst(".gall_writer").attr("data-uid"));
//            System.out.println("nickname  = " + ele.selectFirst(".nickname em").text());
//            System.out.println("date  = " + LocalDateTime.parse(ele.selectFirst(".gall_date").attr("title"), DATE_FORMATTER));


        }catch(IOException e) {
            log.error("IOException -> ", e);
        } catch (Exception e) {
            log.error("sleep 실패 -> ", e);
        }

    }


    private void parseDcPage(Element element) {
        postService.newPostDC(
                Post.newPost(
                        Long.valueOf(element.selectFirst(".gall_num").text()),
                        element.selectFirst(".gall_tit [href]").text(),
                        element.selectFirst(".gall_tit a").absUrl("href"),
                        element.selectFirst(".nickname em").text(),
                        element.selectFirst(".gall_writer").attr("data-uid"),
                        LocalDateTime.parse(element.selectFirst(".gall_date").attr("title"), DATE_FORMATTER)));
    }
}
