package hpPrice.service;

import hpPrice.domain.*;
import hpPrice.common.paging.PageDto;
import hpPrice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService { // TODO 나중에 있는 메서드들 전부 쪼개기
    private final PostRepository postRepository;

    // DC

    public List<PostItem> findPostItems(PageDto pageDto, SearchCond cond) {
        return postRepository.findPagedPostItemsBySearchCond(pageDto, cond);
    }

    public PostItem findPostItem(Long postNum) {
        return postRepository.findPostItemByPostNum(postNum);
    }

    public Post findPost(Long postNum) {
        return postRepository.findPostByPostNum(postNum);
    }

    public Integer countPostItems(SearchCond cond) {
        return postRepository.countPostItemsBySearchCond(cond);
    }


    // NAVER CAFE

    public List<NaverPostItem> findNvPostItems(PageDto pageDto, SearchCond cond, int category) {
        return postRepository.findPagedNvPostItemsBySearchCond(pageDto, cond, category);
    }

    public NaverPostItem findNvPostItem(Long postNum) {
        return postRepository.findNvPostItemByPostNum(postNum);
    }

    public Integer countNvPostItems(SearchCond cond, int category) {
        return postRepository.countNvPostItemsBySearchCond(cond, category);
    }
}
