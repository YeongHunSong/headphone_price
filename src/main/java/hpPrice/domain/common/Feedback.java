package hpPrice.domain.common;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Feedback {
    private Long postNum;

    private String title;

    private String content;

    private LocalDateTime wDate;

    private Boolean isResolved;

    private LocalDateTime resolvedDate;

    private Feedback() {
    }

    private Feedback(String title, String content) {
        this.title = title;
        this.content = content;
    }
}