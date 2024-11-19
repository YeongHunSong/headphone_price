package hp_price.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post {
    private Long postNum;

    private String title;

    private String url;

    private String nickname;

    private String userId;

    private LocalDateTime date;

    private Post() {
    }

    private Post(Long postNum, String title, String url, String nickname, String userId, LocalDateTime date) {
        this.postNum = postNum;
        this.title = title;
        this.url = url;
        this.nickname = nickname;
        this.userId = userId;
        this.date = date;
    }

    public static Post newPost(Long postNum, String title, String url, String nickname, String userId, LocalDateTime date) {
        return new Post(postNum, title, url, nickname, userId, date);
    }
}
