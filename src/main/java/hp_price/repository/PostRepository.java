package hp_price.repository;

import hp_price.domain.Post;

import java.util.List;

public interface PostRepository {

    Post newPost(Post post);

    List<Post> findAll(); // TODO 검색 조건 관련 추가하기
}
