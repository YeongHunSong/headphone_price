package hpPrice.repository;

import hpPrice.domain.Post;
import hpPrice.repository.mybatis.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisPostRepository implements PostRepository {

    private final PostMapper postMapper;

    @Override
    public void newPost(Post post) {
        postMapper.newPost(post);
    }

    @Override
    public Long lastPostNum() {
        return postMapper.lastPostNum();
    }

    @Override
    public List<Post> findAll() {
        return postMapper.findAll();
    }
}
