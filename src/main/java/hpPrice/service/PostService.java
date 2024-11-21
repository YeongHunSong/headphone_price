package hpPrice.service;

import hpPrice.domain.Post;
import hpPrice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void newPostDC(Post post) {
        post.setUserUrl(gallLogDc(post.getUserId()));
        log.info("post = {}", post);
        postRepository.newPost(post);
    }

    public Boolean isCheckDup(Long postNum) {
        return postRepository.isCheckDup(postNum);
    }


//    public Long findByPostNum(Long postNum) {
//        return postRepository.findByPostNum(postNum);
//    }


    private String gallLogDc(String userId) {
        return "https://gallog.dcinside.com/" + userId;
    }
}
