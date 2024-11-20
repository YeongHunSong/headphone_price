package hpPrice.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post {
    private Long postNum;

    private String title;

    private String url;

    private String nickname;

    private String userId;

    private String userUrl; // NullAble

    private LocalDateTime wDate;

    public Post() {
    }

    private Post(Long postNum, String title, String url, String nickname, String userId, LocalDateTime wDate) {
        this.postNum = postNum;
        this.title = title;
        this.url = url;
        this.nickname = nickname;
        this.userId = userId;
        this.wDate = wDate;
    }

    public static Post newPost(Long postNum, String title, String url, String nickname, String userId, LocalDateTime date) {
        return new Post(postNum, title, url, nickname, userId, date);
    }
}
