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

    // MySQL 의 경우 Boolean 을 반환하지 않고, 1, 0으로 반환
    @Override
    public Boolean isCheckDup(Long postNum) {
        return postMapper.isCheckDup(postNum);
    }

    @Override
    public List<Post> findAll() {
        return postMapper.findAll();
    }
}
