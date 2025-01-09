package hpPrice.controller;

import hpPrice.domain.Post;
import hpPrice.domain.PostItem;
import hpPrice.paging.PageControl;
import hpPrice.paging.PageDto;
import hpPrice.search.SearchCond;
import hpPrice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController { // TODO pageView 옵션은 지워도 될 것 같음.

    private final PostService postService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(int.class, "pageView", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    setValue(Integer.parseInt(text));
                } catch (NumberFormatException e) {
                    setValue(50);
                }
            }
        });

        binder.registerCustomEditor(int.class, "page", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    setValue(Integer.parseInt(text));
                } catch (NumberFormatException e) {
                    setValue(1);
                }
            }
        });

        binder.registerCustomEditor(int.class, "postNum", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    setValue(Integer.parseInt(text));
                } catch (NumberFormatException e) {
                    setValue(0);
                }
            }
        });
//        특정 필드 바인딩 제어:
//          setDisallowedFields(): 특정 필드의 바인딩을 금지합니다.
//          setAllowedFields(): 허용할 필드만 지정하여 바인딩합니다.
//        커스텀 포맷터 및 에디터 등록:
//          addCustomFormatter(): 사용자 정의 Formatter 를 등록합니다.
//          registerCustomEditor(): 사용자 정의 PropertyEditor 를 등록합니다.
//        유효성 검사기 추가:
//          addValidators(): 특정 Validator 를 등록하여 객체의 유효성을 검사합니다.
//        데이터 형 변환:
//          요청으로 들어온 문자열 데이터를 객체의 속성 타입에 맞게 변환합니다.
//        특정 객체에만 바인딩 적용:
//          @InitBinder("objectName")을 사용하여 특정 객체에만 바인딩 또는 검증 설정을 적용할 수 있습니다.

    }


    @GetMapping("/dcsff")
    public String dcHeadphone(Model model,
                              @ModelAttribute(name = "pageDto") PageDto pageDto, @ModelAttribute(name = "cond") SearchCond cond) {
        model.addAttribute("pageControl", PageControl.createPage(pageDto, postService.countPostItems(cond)));
        model.addAttribute("postItem", postService.findPostItems(pageDto, cond));
        return "dc/home";

    }

    @GetMapping("/dcsff/{postNum}")
    public String dcHeadphoneDetail(@PathVariable Long postNum, Model model) {
        if (postNum == 0) {
            return "dc/home";
        }

        PostItem postItem = postService.findPostItem(postNum);
        Post post = postService.findPost(postNum);



        model.addAttribute("postItem", postItem);
        model.addAttribute("post", post);
        return "dc/postDetail";
    }


    @ResponseBody
//    @GetMapping("/test")
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
