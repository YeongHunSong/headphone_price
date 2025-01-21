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

    public void savePostItemDC(PostItem postItem) {
        if (postItem.getUserId().isEmpty()) { // 비로그인 계정의 경우 갤로그 공백
            postItem.setUserId("유동");
            postItem.setUserUrl("");
        } else {
            postItem.setUserUrl(gallLogDc(postItem.getUserId()));
        }
        postRepository.newPostItem(postItem);
        log.info("저장한 게시글 - {} {}", postItem.getPostNum(), postItem.getTitle());
    }

    public void newPostDC(Post post) {
        postRepository.newPost(post);
    }


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


    private String gallLogDc(String userId) {
        return "https://gallog.dcinside.com/" + userId;
    }





}
