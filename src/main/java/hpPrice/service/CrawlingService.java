package hpPrice.service;

import hpPrice.domain.Post;
import hpPrice.domain.PostList;
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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Thread.sleep;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingService {

    private final PostService postService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final String DC_HEADPHONE_URL = "https://gall.dcinside.com/mgallery/board/view/?id=newheadphone&no=";
    public static final String DC_IMG_HOST_BEFORE = "dcimg8";
    public static final String DC_IMG_HOST_AFTER = "dcimg3";



//    @Scheduled(fixedDelay = 60000) // 60초 주기로 크롤링
    public void crawlingDcTitle() {
        log.info("크롤링 동작 중 [{}]", LocalDateTime.now().format(DATE_FORMATTER));

        try { // lastPageSearch
            String url = "https://gall.dcinside.com/mgallery/board/lists/?id=newheadphone&search_head=120";
            Connection connection = Jsoup.connect(url); // TODO user-agent 추가
            Document doc = connection.get(); // post()

            String pageUrl = doc.select(".page_end").attr("href");
            if (pageUrl.isEmpty()) { // 파싱 태그값이 변경된 경우, URL 이 변경된 경우
                throw new SQLException();
            }
            int lastPage = Integer.parseInt(pageUrl.substring(pageUrl.indexOf("page=") + 5, pageUrl.indexOf("&search")));
            Long lastPostNum = postService.lastListNum(); // PostService 에서 null 발생 시 0으로 반환 (DB에 저장값이 없는 경우)


            pageLoop: // crawlingAndParsing
            for (int i = 1; i <= lastPage; i++) {
                // ## 게시글 리스트 파싱 ##
                sleep(250);
                Elements elements = Jsoup.connect(url + "&page=" + i).get().select(".gall_list .us-post");
                log.info("현재 파싱 페이지 -> [{}]page", i);
                for (Element element : elements) {
                    if (Long.valueOf(element.selectFirst(".gall_num").text()).equals(lastPostNum) // DB 마지막 저장값과 파싱값이 동일. (일반적인 경우)
                            || Long.parseLong(element.selectFirst(".gall_num").text()) < lastPostNum) { // DB 마지막 저장값보다 파싱값이 작음. (마지막 저장값이 삭제된 경우)
                        log.info("DB 갱신 완료: break 페이지 -> [{}]page", i);
                        break pageLoop; // 필요한 게시글까지 다 받아온 경우
                    }
                    parseDcPage(element); // 리스트에서 한줄씩 착착착 DB에 저장

                    // ## 게시글 내용 파싱 ##
                    sleep(250);
                    Elements els = Jsoup.connect(DC_HEADPHONE_URL + element.selectFirst(".gall_num").text()).get().select(".write_div > *");
                    els.removeIf(ele -> ele.children().is("iframe") // 업로드 동영상 링크 삭제
                                    || (ele.children().hasAttr("src") && ele.children().attr("src").charAt(13) == '5')); // 디시콘 삭제

                    for (Element el : els) {
                        Elements children = el.children();
                        String imageUrl = children.attr("src"); // 업로드 이미지 URL

                        if (children.hasAttr("src") && (imageUrl.charAt(13)) == '8') {
                            children.attr("src", imageUrl.replace(DC_IMG_HOST_BEFORE, DC_IMG_HOST_AFTER)); // 외부에서 불러올 수 있는 이미지 HOST 변경
                        }

                        if (children.is("a")) {
                            children.select("img.og-img").attr("src", children.select("img.og-img").attr("src").replace(DC_IMG_HOST_BEFORE, DC_IMG_HOST_AFTER)); // 링크 썸네일 이미지 HOST 변경
                        }
                    }

                    // DB에 저장하는 코드
                    postService.newPostDC(
                            Post.newPost(
                                    Long.valueOf(element.selectFirst(".gall_num").text()),
                                    els.html()));
                    

                }
            }
        } catch (SQLException e) {
            log.error("데이터값 변경 필요 -> ", e);
        } catch(IOException e) {
            log.error("IOException -> ", e);
        } catch(NullPointerException e) {
            log.error("NullPointerException -> ", e);
        } catch (Exception e) {
            log.error("sleep 실패 -> ", e);
        }
    }


    private void parseDcPage(Element element) {
        postService.newPostListDC(
                PostList.newPostList(
                        Long.valueOf(element.selectFirst(".gall_num").text()),
                        element.selectFirst(".gall_tit [href]").text(),
                        element.selectFirst(".gall_tit a").absUrl("href"),
                        element.selectFirst(".nickname em").text(),
                        element.selectFirst(".gall_writer").attr("data-uid"),
                        LocalDateTime.parse(element.selectFirst(".gall_date").attr("title"), CrawlingService.DATE_FORMATTER)));
    }
}
