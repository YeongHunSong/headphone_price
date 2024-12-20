package hpPrice.service;

import hpPrice.domain.ErrorPost;
import hpPrice.domain.Post;
import hpPrice.domain.PostList;
import hpPrice.search.SearchCond;
import hpPrice.paging.PageDto;
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

    public void newPostListDC(PostList postList) {
        if (postList.getUserId().isEmpty()) { // 비로그인 계정의 경우 갤로그 공백
            postList.setUserId("유동");
            postList.setUserUrl("");
        } else {
            postList.setUserUrl(gallLogDc(postList.getUserId()));
        }
        log.info("저장한 게시글 - {} {}", postList.getPostNum(), postList.getTitle());
        postRepository.newPostList(postList);
    }

    public void newPostDC(Post post) {
        postRepository.newPost(post);
    }

    public Long lastPostNum() {
        // lastPostNum 이 null 인 경우(=DB에 아무 값이 없음), 0으로 반환
        Long lastPostNum = postRepository.lastPostNum();
        return lastPostNum == null ? 0L : lastPostNum;
    }

    public Integer postCount(SearchCond cond) {
        return postRepository.totalCount(cond);
    }

    public List<PostList> findAll(PageDto pageDto, SearchCond cond) {
        return postRepository.findAll(pageDto, cond);
    }

    public void errorReport(ErrorPost errorPost) {
        postRepository.errorReport(errorPost);
    }

    public Long errorCheck() {
        return postRepository.errorCheck();
    }

    public void resolveError(Long postNum, Long errorNum) {
        postRepository.deleteErrorPostList(postNum);
        postRepository.resolveError(errorNum);
    }




    private String gallLogDc(String userId) {
        return "https://gallog.dcinside.com/" + userId;
    }
}
