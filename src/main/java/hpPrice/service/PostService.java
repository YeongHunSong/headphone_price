package hpPrice.service;

import hpPrice.domain.Post;
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

    public void newPostDC(Post post) {
        if (post.getUserId().isEmpty()) { // 비로그인 계정의 경우 갤로그 공백
            post.setUserId("유동");
            post.setUserUrl("");
        } else {
            post.setUserUrl(gallLogDc(post.getUserId()));
        }
        log.info("저장한 게시글 = {}", post.getTitle());
        postRepository.newPost(post);
    }

    public Long lastPostNum() {
        Long lastPostNum = postRepository.lastPostNum();
        // lastPostNum 이 null 인 경우(=DB에 아무 값이 없음), 0으로 반환
        return lastPostNum == null ? 0 : lastPostNum;
    }

    public int postCount() {
        return postRepository.totalCount();
    }

    public List<Post> findAll(PageDto pageDto) {
        return postRepository.findAll(pageDto);
    }


    private String gallLogDc(String userId) {
        return "https://gallog.dcinside.com/" + userId;
    }
}
