package hpPrice.repository;

import hpPrice.domain.Post;

import java.util.List;

public interface PostRepository {

    void newPost(Post post);

    // MySQL 의 경우 Boolean 을 반환하지 않고, 1, 0으로 반환
    Boolean isCheckDup(Long postNum);

    Long lastPostNum();

    List<Post> findAll(); // TODO 검색 조건 관련 추가하기
}
