package hpPrice.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NaverPostItem {
    private Long postNum;

    private String title;

    private String url;

    private String nickname;

    private LocalDateTime wDate;

    private String memLevel;

    private NaverPostItem() {
    }

    private NaverPostItem(Long postNum, String title, String url, String nickname, String memLevel, LocalDateTime wDate) {
        this.postNum = postNum;
        this.title = title;
        this.url = url;
        this.nickname = nickname;
        this.memLevel = memLevel;
        this.wDate = wDate;
    }

    public static NaverPostItem newPostItem(Long postNum, String title, String url, String nickname, String memLevel, LocalDateTime wDate) {
        return new NaverPostItem(postNum, title, url, nickname, memLevel, wDate);
    }

}
