package hpPrice.domain;

import lombok.Data;

@Data
public class ErrorDto {

    private Long errorNum;

    private Long postNum;

    private ErrorDto() {
    }
}
