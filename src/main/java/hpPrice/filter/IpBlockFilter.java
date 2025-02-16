package hpPrice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static hpPrice.common.ip.IpAddressUtil.*;

@Slf4j
@Component
public class IpBlockFilter extends OncePerRequestFilter {

    private final List<String> allowedPaths =
            Arrays.asList("/css/**", "/js/**", "/error/**", "/favicon/**", "/feedback");

    private final Set<String> blockedIps = new HashSet<>
            (Arrays.asList("45.148.10.90", "195.178.110.163", "118.235.80.48"));


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 허용된 경로인지 확인
        if (isAllowedPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        if (blockedIps.contains(clientIp)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            log.info("차단된 IP 접근 시도 -> {}", clientIp);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAllowedPath(HttpServletRequest request) {
        return allowedPaths.stream().anyMatch(path ->
                new AntPathMatcher().match(path, request.getRequestURI()));
    }
}
