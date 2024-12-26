package hpPrice.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorPost {
    private Long postNum;

    private String errorDesc;

    private LocalDateTime errorDate;

    private Boolean isResolved;

    private LocalDateTime resolvedDate;

    private ErrorPost() {
    }

    private ErrorPost(Long postNum, String errorDesc) {
        this.postNum = postNum;
        this.errorDesc = errorDesc;
    }


    public static ErrorPost errorReport(Long postNum, String errorDesc) {
        return new ErrorPost(postNum, errorDesc);
    }
}
