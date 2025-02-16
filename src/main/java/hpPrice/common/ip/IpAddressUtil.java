package hpPrice.common.ip;

import jakarta.servlet.http.HttpServletRequest;

public class IpAddressUtil {
    private IpAddressUtil() {}

    public static String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
