package hpPrice.filter;

import hpPrice.common.ipBlock.IpAddressUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class IpBlockFilter extends OncePerRequestFilter {

    private final Set<String> blockedIps = new HashSet<>
            (Arrays.asList("45.148.10.90", "195.178.110.163", "118.235.80.48"));

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientIp = IpAddressUtil.getClientIp(request);
        if (blockedIps.contains(clientIp)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            log.info("차단된 IP 접근 시도 -> " + clientIp);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
