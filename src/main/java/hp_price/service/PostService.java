package hp_price.service;

import hp_price.domain.Post;
import hp_price.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void newPostDC(Post post) {
        post.setUrl(fullUrlDC(post.getUrl()));
        postRepository.newPost(post);
    }




    private String fullUrlDC(String url) {
        return "https://gall.dcinside.com" + url;
    }
}
