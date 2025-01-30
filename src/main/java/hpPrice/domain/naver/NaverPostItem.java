package hpPrice.domain.naver;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NaverPostItem {
    private Long postNum;

    private String title;

    private Integer category;

    private String url;

    private String nickname;

    private LocalDateTime wDate;

    private String memLevelIcon;

    private String memLevel;

    private String price;

    private NaverPostItem() {
    }

    private NaverPostItem(Long postNum, String title, Integer category, String url, String nickname, String memLevelIcon, String memLevel, LocalDateTime wDate, String price) {
        this.postNum = postNum;
        this.title = title;
        this.category = category;
        this.url = url;
        this.nickname = nickname;
        this.memLevelIcon = memLevelIcon;
        this.memLevel = memLevel;
        this.wDate = wDate;
        this.price = price;
    }

    public static NaverPostItem newPostItem(Long postNum, String title, Integer category, String url, String nickname, String memLevelIcon, String memLevel, LocalDateTime wDate, String price) {
        return new NaverPostItem(postNum, title, category, url, nickname, memLevelIcon, memLevel, wDate, price);
    }

}
