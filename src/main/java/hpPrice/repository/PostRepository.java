package hpPrice.repository;

import hpPrice.domain.Post;
import hpPrice.paging.PageDto;

import java.util.List;

public interface PostRepository {

    void newPost(Post post);

    Long lastPostNum();

    int totalCount();

    List<Post> findAll(PageDto pageDto); // TODO 검색 조건 관련 추가하기
}
