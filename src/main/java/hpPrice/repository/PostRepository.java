package hpPrice.repository;

import hpPrice.domain.ErrorPost;
import hpPrice.domain.Post;
import hpPrice.domain.PostItem;
import hpPrice.domain.ErrorDto;
import hpPrice.search.SearchCond;
import hpPrice.paging.PageDto;

import java.util.List;

public interface PostRepository {

    // INSERT
    void newPostItem(PostItem postItem);

    void newPost(Post post);

    void newErrorPost(ErrorPost errorPost);


    // SELECT
    List<PostItem> findPagedPostItemsBySearchCond(PageDto pageDto, SearchCond cond);

    PostItem findPostItemByPostNum(Long postNum);

    Post findPostByPostNum(Long postNum);

    ErrorDto findErrorPost();

    Long findLatestPostNum();

    Integer countPostItemsBySearchCond(SearchCond cond);


    // UPDATE
    void resolveError(Long errorNum);


    // DELETE
    void deletePostItemByPostNum(Long postNum);

}
