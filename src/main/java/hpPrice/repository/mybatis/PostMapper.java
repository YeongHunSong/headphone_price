package hpPrice.repository.mybatis;

import hpPrice.domain.Post;
import hpPrice.paging.PageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    void newPost(Post post);

    Long lastPostNum();

    int totalCount();

    List<Post> findAll(@Param("pageDto") PageDto pageDto);
}
