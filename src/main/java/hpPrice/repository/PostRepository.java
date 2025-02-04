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

import java.util.List;

public interface PostRepository {

    // INSERT
    void newPostItem(PostItem postItem);

    void newPost(Post post);

    void newErrorPost(ErrorPost errorPost);

    void newLoginCookies(LoginCookies loginCookies);

    void newNaverPostItem(NaverPostItem postItem);

    void newNaverPost(Post post);

    void newFeedback(Feedback feedback);


    // SELECT
    List<PostItem> findPagedPostItemsBySearchCond(PageDto pageDto, SearchCond cond);

    PostItem findPostItemByPostNum(Long postNum);

    Post findPostByPostNum(Long postNum);

    ErrorDto findErrorPost();

    Long findLatestPostNum();

    Integer countPostItemsBySearchCond(SearchCond cond);

    String findLatestLoginCookiesByDesc(String desc);

    Long findLatestNaverPostNum(Integer category);

    List<NaverPostItem> findPagedNaverPostItemsBySearchCond(PageDto pageDto, SearchCond cond, Integer category);

    NaverPostItem findNaverPostItemByPostNum(Long postNum);

    Post findNaverPostByPostNum(Long postNum);

    Integer countNaverPostItemsBySearchCond(SearchCond cond, Integer category);


    // UPDATE
    void resolveError(Long errorNum);


    // DELETE
    void deletePostItemByPostNum(Long postNum);

}
