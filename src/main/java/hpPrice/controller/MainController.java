package hpPrice.controller;

import hpPrice.domain.Post;
import hpPrice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;

    @GetMapping("/dcHp")
    public String dcHeadphone(Model model) {


        model.addAttribute("postList", postService.findAll());

        return "dc/dcHeadphonePrice";
    }


}
