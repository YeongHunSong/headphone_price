package hpPrice.domain.common;

import lombok.Data;

@Data
public class Post {
    private Long postNum;

    private String content;

    private Post() {
    }

    private Post(Long postNum, String content) {
        this.postNum = postNum;
        this.content = content;
    }

    public static Post newPost(Long postNum, String content) {
        return new Post(postNum, content);
    }
}
