package hpPrice.repository;

import hpPrice.domain.Post;

import java.util.List;

public interface PostRepository {

    void newPost(Post post);

    Long lastPostNum();

    List<Post> findAll(); // TODO 검색 조건 관련 추가하기
}
