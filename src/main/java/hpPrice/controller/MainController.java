package hpPrice.controller;

import hpPrice.paging.PageControl;
import hpPrice.paging.PageDto;
import hpPrice.search.SearchCond;
import hpPrice.service.CrawlingService;
import hpPrice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

import static java.lang.Thread.sleep;
import static hpPrice.service.CrawlingService.*;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;
    private final CrawlingService crawlingService;

    // TODO image 불러오는 Mapping 추가

    @GetMapping("/dcheadphone")
    public String dcHeadphone(Model model, @ModelAttribute(name = "pageDto") PageDto pageDto, @ModelAttribute(name = "cond") SearchCond cond) {
        model.addAttribute("pageControl", PageControl.create(pageDto, postService.postCount(cond)));
        model.addAttribute("postList", postService.findAll(pageDto, cond));
        return "dc/dcHeadphonePrice";
    }

    @GetMapping("/dcheadphone/{postNum}")
    public String dcHeadphoneDetail(@PathVariable Long postNum, Model model) {
        // TODO 글 내용 집어넣기
        // TODO padding 5px

        return "dc/postDetail";
    }


    @ResponseBody
    @GetMapping("/test")
    public String test() {
//        try { // TODO TEST 필요한 게시글 : 1177280
//
//
//            // 링크: 1178263
//            // 유동: 1177472
//            // 디시콘: 1180893
//            // 동영상: 1176410 1181651
//            // ## 게시글 내용 파싱 ##
//            sleep(300);
//            Elements dcHeadphonePost = Jsoup.connect(DC_POST_URL + DC_GALL_NAME + DC_POST_QUERY + "1180893")
//                    .userAgent(USER_AGENT)
//                    .timeout(TIME_OUT)
//                    .get()
//                    .select(".write_div > *");
//            System.out.println("원본 \n" + dcHeadphonePost.outerHtml());
//            dcHeadphonePost.removeIf(postLine -> postLine.select("iframe").is("iframe")); // 업로드 동영상 링크 삭제
//
//            for (Element postLine : dcHeadphonePost) {
//                Elements image = postLine.select("img");
//                String imageUrl = image.attr("src");
//
//                if (!(imageUrl.isEmpty())) { // 이미지가 있는지 확인 후 외부에서 불러올 수 있는 이미지 HOST 변경
//                    switch ((imageUrl.charAt(13))) {
//                        case '3': // 업로드 이미지 링크
//                            break;
//                        case '4': // 업로드 이미지 링크
//                            break;
//                        case '5': // 디시콘 이미지 링크
//                            image.attr("src", imageUrl.replace(DC_CON_HOST_BEFORE, DC_CON_HOST_AFTER));
//                            break;
//                        case '8': // 업로드 이미지 링크
//                            image.attr("src", imageUrl.replace(DC_IMG_HOST_BEFORE, DC_IMG_HOST_AFTER));
//                            break;
//                        default:
//                            throw new IOException("이상한 URL => " + imageUrl);
//                    }
//                }
//
//                } // for
//            System.out.println("결과물 \n" + dcHeadphonePost.outerHtml());
//            return dcHeadphonePost.toString();
//
//        } catch (IOException e) { // timeOut, 데이터 없음 등등
//            log.error("IOException -> ", e);
//        } catch (NullPointerException e) {
//            log.error("NullPointerException -> ", e);
//        } catch (InterruptedException e) {
//            log.error("sleep 실패 -> ", e);
//        } catch (Exception e) {
//            log.error("Exception -> ", e);
//        }
        return "<img src=\"https://dcimg3.dcinside.co.kr/viewimage.php?id=3ebbd6&amp;no=24b0d769e1d32ca73de886fa11d02831fdb1643214b32d9bb1b5ab3e58bb89b1216bffb62d1ebdb9618bca1b2397cbbb31cc16f365ff7ff1b8edce6f50754e1845bffcae\"/>";
    } // try-catch
}
