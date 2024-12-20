package hpPrice.repository.mybatis;

import hpPrice.domain.ErrorPost;
import hpPrice.domain.Post;
import hpPrice.domain.PostList;
import hpPrice.search.SearchCond;
import hpPrice.paging.PageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    void newPostList(PostList postList);

    void newPost(Post post);

    Long lastPostNum();

    Integer totalCount(@Param("cond")SearchCond cond);

    List<PostList> findAll(@Param("pageDto") PageDto pageDto,
                           @Param("cond") SearchCond cond);

    void errorReport(ErrorPost errorPost);

    Long errorCheck();

    void deleteErrorPostList(Long postNum);

    void resolveError(Long errorNum);
}
