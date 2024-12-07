package hpPrice.repository.mybatis;

import hpPrice.domain.Post;
import hpPrice.search.SearchCond;
import hpPrice.paging.PageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    void newPost(Post post);

    Long lastPostNum();

    int totalCount(@Param("cond")SearchCond cond);

    List<Post> findAll(@Param("pageDto") PageDto pageDto,
                       @Param("cond") SearchCond cond);
}
