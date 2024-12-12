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
    
    // TODO image 불러오는 Mapping 추가

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
                Long lastPostNum = postService.lastListNum(); // PostService 에서 null 발생 시 0으로 반환 (DB에 저장값이 없는 경우)


                // TODO TEST 필요한 게시글 : 1177280 / 1177886 - 1178263 / 1176410 / 1177472
                // TODO padding 5px
                pageLoop: // crawlingAndParsing
                    sleep(300);
                    Elements elements = Jsoup.connect(url).get().select(".gall_list .us-post");
                    for (Element element : elements) {
                        sleep(300);
                        Elements els = Jsoup.connect(CrawlingService.DC_HEADPHONE_URL + element.selectFirst(".gall_num").text()).get()
                                .select(".write_div > *");
                        els.removeIf(
                                ele -> ele.children().is("iframe") || // 업로드 동영상 링크 삭제
                                (ele.children().hasAttr("src") && ele.children().attr("src").charAt(13) == '5')); // 디시콘 삭제

                        for (Element ele : els) {
                            Elements children = ele.children();
                            String imageUrl = children.attr("src"); // 업로드 이미지 URL
                            String linkImage = children.select("img.og-img").attr("src"); // 링크 섬네일 이미지 URL

                            if (children.hasAttr("src") && (imageUrl.charAt(13)) == '8') {
                                children.attr("src", imageUrl.replace(CrawlingService.DC_IMG_HOST_BEFORE, CrawlingService.DC_IMG_HOST_AFTER)); // 외부에서 불러올 수 있는 이미지 HOST 변경
                            }
                            
                            if (children.is("a")) { 
                                children.select("img.og-img").attr("src", linkImage.replace(CrawlingService.DC_IMG_HOST_BEFORE, CrawlingService.DC_IMG_HOST_AFTER)); // 링크 썸네일 이미지 HOST 변경
                            }
                        }
                    }


//                    el.childNodeSize(); // 보유한 자식의 수
//                    el.lastChild(); // 마지막이면 null, 마지막이 아니면 child 반환
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
