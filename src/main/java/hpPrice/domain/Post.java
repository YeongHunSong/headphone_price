package hpPrice.domain;

import lombok.Data;

@Data
public class Post {
    private Long postNum;

    private String content;

    public Post() {
    }

    private Post(Long postNum, String content) {
        this.postNum = postNum;
        this.content = content;
    }

    public static Post newPost(Long postNum, String content) {
        return new Post(postNum, content);
    }
}
