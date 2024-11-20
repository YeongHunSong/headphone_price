package hpPrice.repository.mybatis;

import hpPrice.domain.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {

    void newPost(Post post);

    // TODO MySQL 의 경우 Boolean 을 반환하지 않고, 1, 0으로 반환
    Boolean isCheckDup(Long postNum);

    List<Post> findAll();
}
