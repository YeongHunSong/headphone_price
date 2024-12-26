package hpPrice.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostList {
    private Long postNum;

    private String title;

    private String modTitle; // TODO 언젠가는...

    private String url;

    private String nickname;

    private String userId;

    private String userUrl; // NullAble - 유동

    private LocalDateTime wDate;

    private PostList() {
    }

    private PostList(Long postNum, String title, String url, String nickname, String userId, LocalDateTime wDate) {
        this.postNum = postNum;
        this.title = title;
        this.url = url;
        this.nickname = nickname;
        this.userId = userId;
        this.wDate = wDate;
    }

    public static PostList newPostList(Long postNum, String title, String url, String nickname, String userId, LocalDateTime date) {
        return new PostList(postNum, title, url, nickname, userId, date);
    }

}
