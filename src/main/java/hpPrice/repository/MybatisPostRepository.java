package hpPrice.repository;

import hpPrice.common.paging.PageDto;
import hpPrice.domain.common.Feedback;
import hpPrice.domain.common.SearchCond;
import hpPrice.domain.dc.ErrorDto;
import hpPrice.domain.dc.ErrorPost;
import hpPrice.domain.common.Post;
import hpPrice.domain.dc.PostItem;
import hpPrice.domain.naver.LoginCookies;
import hpPrice.domain.naver.NaverPostItem;
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
    public void newLoginCookies(LoginCookies loginCookies) {
        postMapper.newLoginCookies(loginCookies);
    }

    @Override
    public void newNaverPostItem(NaverPostItem postItem) {
        postMapper.newNaverPostItem(postItem);
    }

    @Override
    public void newNaverPost(Post post) { postMapper.newNaverPost(post);}

    @Override
    public void newFeedback(Feedback feedback) {
        postMapper.newFeedback(feedback);
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
    public String findLatestLoginCookiesByDesc(String desc) {
        return postMapper.findLatestLoginCookiesByDesc(desc);
    }

    @Override
    public Long findLatestNaverPostNum(Integer category) {
        return postMapper.findLatestNaverPostNum(category);
    }

    @Override
    public List<NaverPostItem> findPagedNaverPostItemsBySearchCond(PageDto pageDto, SearchCond cond, Integer category) {
        return postMapper.findPagedNaverPostItemsBySearchCond(pageDto, cond, category);
    }

    @Override
    public NaverPostItem findNaverPostItemByPostNum(Long postNum) {
        return postMapper.findNaverPostItemByPostNum(postNum);
    }

    @Override
    public Post findNaverPostByPostNum(Long postNum) {
        return postMapper.findNaverPostByPostNum(postNum);
    }

    @Override
    public Integer countNaverPostItemsBySearchCond(SearchCond cond, Integer category) {
        return postMapper.countNaverPostItemsBySearchCond(cond, category);
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
