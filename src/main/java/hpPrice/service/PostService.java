package hpPrice.service;

import hpPrice.domain.Post;
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
        post.setUserUrl(gallLogDc(post.getUserId()));
        log.info("저장한 게시글 = {}", post.getTitle());
        postRepository.newPost(post);
    }

    public Boolean isCheckDup(Long postNum) {
        return postRepository.isCheckDup(postNum);
    }

    public Long lastPostNum() {
        return postRepository.lastPostNum();
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }



    private String gallLogDc(String userId) {
        return "https://gallog.dcinside.com/" + userId;
    }
}
