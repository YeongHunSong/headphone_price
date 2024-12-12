package hpPrice.repository;

import hpPrice.domain.Post;
import hpPrice.domain.PostList;
import hpPrice.search.SearchCond;
import hpPrice.paging.PageDto;
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
    public void newPostList(PostList postList) {
        postMapper.newPostList(postList);
    }

    @Override
    public void newPost(Post post) {
        postMapper.newPost(post);
    }

    @Override
    public Long lastListNum() {
        return postMapper.lastListNum();
    }

    @Override
    public int totalCount(SearchCond cond) {
        return postMapper.totalCount(cond);
    }

    @Override
    public List<PostList> findAll(PageDto pageDto, SearchCond cond) {
        return postMapper.findAll(pageDto, cond);
    }
}
