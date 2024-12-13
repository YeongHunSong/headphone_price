package hpPrice.repository;

import hpPrice.domain.Post;
import hpPrice.domain.PostList;
import hpPrice.search.SearchCond;
import hpPrice.paging.PageDto;

import java.util.List;

public interface PostRepository {

    void newPostList(PostList postList);

    void newPost(Post post);

    Integer lastPostNum();

    int totalCount(SearchCond cond);

    List<PostList> findAll(PageDto pageDto, SearchCond cond);
}
