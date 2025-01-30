package hpPrice.domain.dc;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostItem {
    private Long postNum;

    private String title;

    private String modTitle; // TODO 언젠가는...

    private String url;

    private String nickname;

    private String userId;

    private String userUrl; // NullAble - 유동

    private LocalDateTime wDate;

    private PostItem() {
    }

    private PostItem(Long postNum, String title, String url, String nickname, String userId, LocalDateTime wDate) {
        this.postNum = postNum;
        this.title = title;
        this.url = url;
        this.nickname = nickname;
        this.userId = userId;
        this.wDate = wDate;
    }

    public static PostItem newPostItem(Long postNum, String title, String url, String nickname, String userId, LocalDateTime wDate) {
        return new PostItem(postNum, title, url, nickname, userId, wDate);
    }

}
