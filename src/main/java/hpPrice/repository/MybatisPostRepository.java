package hpPrice.repository;

import hpPrice.domain.ErrorPost;
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
    public Long lastPostNum() {
        return postMapper.lastPostNum();
    }

    @Override
    public Integer totalCount(SearchCond cond) {
        return postMapper.totalCount(cond);
    }

    @Override
    public List<PostList> findAll(PageDto pageDto, SearchCond cond) {
        return postMapper.findAll(pageDto, cond);
    }

    @Override
    public void errorReport(ErrorPost errorPost) {
        postMapper.errorReport(errorPost);
    }

    @Override
    public Long errorCheck() {
        return postMapper.errorCheck();
    }

    @Override
    public void deleteErrorPostList(Long postNum) {
        postMapper.deleteErrorPostList(postNum);
    }

    @Override
    public void resolveError(Long errorNum) {
        postMapper.resolveError(errorNum);
    }

}
