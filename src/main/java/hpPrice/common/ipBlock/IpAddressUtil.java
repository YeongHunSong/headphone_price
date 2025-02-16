package hpPrice.common.ipBlock;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IpAddressUtil {
    private IpAddressUtil() {}

    public static String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public static boolean isAllowedPath(HttpServletRequest request) {
        return allowedPaths.stream().anyMatch(path ->
                new AntPathMatcher().match(path, request.getRequestURI()));
    }










    public static final Set<String> blockedIps = new HashSet<>
            (Arrays.asList("45.148.10.90", "195.178.110.163", "118.235.80.48"));

    public static final List<String> allowedPaths =
            Arrays.asList("/css/**", "/js/**", "/error/**", "/favicon/**", "/feedback");
}
