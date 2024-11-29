package hpPrice.controller;

import hpPrice.paging.PageControl;
import hpPrice.paging.PageDto;
import hpPrice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;

    @GetMapping("/dcHp")
    public String dcHeadphone(Model model, @ModelAttribute(name = "pageDto") PageDto pageDto) {

        model.addAttribute("pageControl", PageControl.create(pageDto, postService.postCount()));
        model.addAttribute("postList", postService.findAll(pageDto));

        return "dc/dcHeadphonePrice";
    }


}
