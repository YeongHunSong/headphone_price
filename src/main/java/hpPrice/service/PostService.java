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
public class PostService {

    private final PostRepository postRepository;

    public void newPostItemDC(PostItem postItem) {
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

    public void reportError(ErrorPost errorPost) {
        postRepository.newErrorPost(errorPost);
    }

    public void storeLoginCookies(LoginCookies loginCookies) {
        postRepository.newLoginCookies(loginCookies);
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

    public ErrorDto errorCheck() {
        return postRepository.findErrorPost();
    }

    public Long latestPostNum() {
        // latestPostNum 이 null 인 경우(=DB에 아무 값이 없음), 0으로 반환
        Long latestPostNum = postRepository.findLatestPostNum();
        return latestPostNum == null ? 0L : latestPostNum;
    }

    public Integer countPostItems(SearchCond cond) {
        return postRepository.countPostItemsBySearchCond(cond);
    }

    public String latestLoginCookies(String desc) {
        return postRepository.findLatestLoginCookiesByDesc(desc);
    }

    public void resolveError(Long postNum, Long errorNum) {
        postRepository.deletePostItemByPostNum(postNum);
        postRepository.resolveError(errorNum);
    }


    private String gallLogDc(String userId) {
        return "https://gallog.dcinside.com/" + userId;
    }
}
