package hpPrice.repository.mybatis;

import hpPrice.domain.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {

    void newPost(Post post);

    Long lastPostNum();

    List<Post> findAll();
}
