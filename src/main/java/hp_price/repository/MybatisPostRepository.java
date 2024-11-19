package hp_price.repository;

import hp_price.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisPostRepository implements PostRepository {
    
    // TODO mapper 추가
    
    @Override
    public Post newPost(Post post) {
        return null;
    }

    @Override
    public List<Post> findAll() {
        return List.of();
    }
}
