package hpPrice.controller;

import hpPrice.paging.PageControl;
import hpPrice.paging.PageDto;
import hpPrice.search.SearchCond;
import hpPrice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;

    @GetMapping("/dcheadphone")
    public String dcHeadphone(Model model, @ModelAttribute(name = "pageDto") PageDto pageDto, @ModelAttribute(name = "cond")SearchCond cond) {

        model.addAttribute("pageControl", PageControl.create(pageDto, postService.postCount(cond)));
        model.addAttribute("postList", postService.findAll(pageDto, cond));

        return "dc/dcHeadphonePrice";
    }


}
