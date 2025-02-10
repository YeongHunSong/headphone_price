package hpPrice.domain.common;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Visitor {

    private Long id;
    private LocalDateTime visitDate;
    private String ipAddress;
    private String requestUrl;

    private Visitor() {}

    private Visitor(String ipAddress, String requestUrl) {
        this.ipAddress = ipAddress;
        this.requestUrl = requestUrl;
    }

    public static Visitor newVisitor(String ipAddress, String requestUrl) {
        return new Visitor(ipAddress, requestUrl);
    }
}
