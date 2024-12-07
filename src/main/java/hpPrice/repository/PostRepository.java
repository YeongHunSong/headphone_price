package hpPrice.repository;

import hpPrice.domain.Post;
import hpPrice.search.SearchCond;
import hpPrice.paging.PageDto;

import java.util.List;

public interface PostRepository {

    void newPost(Post post);

    Long lastPostNum();

    int totalCount(SearchCond cond);

    List<Post> findAll(PageDto pageDto, SearchCond cond);
}
