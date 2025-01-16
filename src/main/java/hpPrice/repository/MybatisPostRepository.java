package hpPrice.repository;

import hpPrice.domain.ErrorPost;
import hpPrice.domain.Post;
import hpPrice.domain.PostItem;
import hpPrice.domain.ErrorDto;
import hpPrice.domain.SearchCond;
import hpPrice.common.paging.PageDto;
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
    public void newPostItem(PostItem postItem) {
        postMapper.newPostItem(postItem);
    }

    @Override
    public void newPost(Post post) {
        postMapper.newPost(post);
    }

    @Override
    public void newErrorPost(ErrorPost errorPost) {
        postMapper.newErrorPost(errorPost);
    }


    @Override
    public List<PostItem> findPagedPostItemsBySearchCond(PageDto pageDto, SearchCond cond) {
        return postMapper.findPagedPostItemsBySearchCond(pageDto, cond);
    }

    @Override
    public PostItem findPostItemByPostNum(Long postNum) {
        return postMapper.findPostItemByPostNum(postNum);
    }

    @Override
    public Post findPostByPostNum(Long postNum) {
        return postMapper.findPostByPostNum(postNum);
    }

    @Override
    public ErrorDto findErrorPost() {
        return postMapper.findErrorPost();
    }

    @Override
    public Long findLatestPostNum() {
        return postMapper.findLatestPostNum();
    }

    @Override
    public Integer countPostItemsBySearchCond(SearchCond cond) {
        return postMapper.countPostItemsBySearchCond(cond);
    }


    @Override
    public void resolveError(Long errorNum) {
        postMapper.resolveError(errorNum);
    }


    @Override
    public void deletePostItemByPostNum(Long postNum) {
        postMapper.deletePostItemByPostNum(postNum);
    }

}
