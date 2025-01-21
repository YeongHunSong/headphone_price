package hpPrice.repository;

import hpPrice.domain.*;
import hpPrice.common.paging.PageDto;

import java.util.List;

public interface PostRepository {

    // INSERT
    void newPostItem(PostItem postItem);

    void newPost(Post post);

    void newErrorPost(ErrorPost errorPost);

    void newLoginCookies(LoginCookies loginCookies);

    void newNaverPostItem(NaverPostItem postItem);


    // SELECT
    List<PostItem> findPagedPostItemsBySearchCond(PageDto pageDto, SearchCond cond);

    PostItem findPostItemByPostNum(Long postNum);

    Post findPostByPostNum(Long postNum);

    ErrorDto findErrorPost();

    Long findLatestPostNum();

    Integer countPostItemsBySearchCond(SearchCond cond);

    String findLatestLoginCookiesByDesc(String desc);


    // UPDATE
    void resolveError(Long errorNum);


    // DELETE
    void deletePostItemByPostNum(Long postNum);

}
