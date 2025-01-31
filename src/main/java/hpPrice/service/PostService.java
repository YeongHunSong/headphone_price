package hpPrice.service;

import hpPrice.common.paging.PageDto;
import hpPrice.domain.common.Post;
import hpPrice.domain.common.SearchCond;
import hpPrice.domain.dc.PostItem;
import hpPrice.domain.naver.NaverPostItem;
import hpPrice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
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

    public List<NaverPostItem> findNaverPostItems(PageDto pageDto, SearchCond cond, int category) {
        return postRepository.findPagedNaverPostItemsBySearchCond(pageDto, cond, category);
    }

    public NaverPostItem findNaverPostItem(Long postNum) {
        return postRepository.findNaverPostItemByPostNum(postNum);
    }

    public Post findNaverPost(Long postNum) {
        return postRepository.findNaverPostByPostNum(postNum);
    }

    public Integer countNvPostItems(SearchCond cond, int category) {
        return postRepository.countNaverPostItemsBySearchCond(cond, category);
    }
}
