package hpPrice.repository.mybatis;

import hpPrice.common.paging.PageDto;
import hpPrice.domain.common.Feedback;
import hpPrice.domain.common.SearchCond;
import hpPrice.domain.dc.ErrorDto;
import hpPrice.domain.dc.ErrorPost;
import hpPrice.domain.common.Post;
import hpPrice.domain.dc.PostItem;
import hpPrice.domain.naver.LoginCookies;
import hpPrice.domain.naver.NaverPostItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostMapper {

    // INSERT
    void newPostItem(PostItem postItem);

    void newPost(Post post);

    void newErrorPost(ErrorPost errorPost);

    void newLoginCookies(LoginCookies loginCookies);

    void newNaverPostItem(NaverPostItem postItem);

    void newNaverPost(Post post);

    void newFeedback(Feedback feedback);


    // SELECT
    List<PostItem> findPagedPostItemsBySearchCond(@Param("pageDto") PageDto pageDto, @Param("cond") SearchCond cond);

    PostItem findPostItemByPostNum(Long postNum);

    Post findPostByPostNum(Long postNum);

    ErrorDto findErrorPost();

    Long findLatestPostNum();

    Integer countPostItemsBySearchCond(@Param("cond")SearchCond cond);

    String findLatestLoginCookiesByDesc(String desc);

    Long findLatestNaverPostNum(Integer category);

    List<NaverPostItem> findPagedNaverPostItemsBySearchCond(@Param("pageDto") PageDto pageDto, @Param("cond") SearchCond cond, @Param("category") Integer category);

    NaverPostItem findNaverPostItemByPostNum(Long postNum);

    Post findNaverPostByPostNum(Long postNum);

    Integer countNaverPostItemsBySearchCond(@Param("cond")SearchCond cond, @Param("category") Integer category);



    // UPDATE
    void resolveError(Long errorNum);


    // DELETE
    void deletePostItemByPostNum(Long postNum);

}
