package hpPrice.domain.common;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Visitor {

    private Long id;
    private LocalDateTime visitDate;
    private String ipAddress;
    private String requestUri;

    private Visitor() {}

    private Visitor(String ipAddress, String requestUri) {
        this.ipAddress = ipAddress;
        this.requestUri = requestUri;
    }

    public static Visitor newVisitor(String ipAddress, String requestUrl) {
        return new Visitor(ipAddress, requestUrl);
    }
}
