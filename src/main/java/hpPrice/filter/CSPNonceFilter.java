package hpPrice.filter;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CSPNonceFilter extends GenericFilterBean {
    private static final int NONCE_SIZE = 32; // 256비트(권장)
    private static final String CSP_NONCE_ATTRIBUTE = "cspNonce";
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String existingNonce = (String) request.getAttribute(CSP_NONCE_ATTRIBUTE);
        if (existingNonce == null) {
            // 새 nonce 생성
            byte[] nonceArray = new byte[NONCE_SIZE];
            secureRandom.nextBytes(nonceArray);
            existingNonce = Base64.getEncoder().encodeToString(nonceArray);
            request.setAttribute(CSP_NONCE_ATTRIBUTE, existingNonce);
        }
        chain.doFilter(request, new CSPNonceResponseWrapper(response, existingNonce));
    }

    // 응답 헤더 처리 래퍼 클래스
    public static class CSPNonceResponseWrapper extends HttpServletResponseWrapper {
        private final String nonce;

        public CSPNonceResponseWrapper(HttpServletResponse response, String nonce) {
            super(response);
            this.nonce = nonce;
        }

        @Override
        public void setHeader(String name, String value) {
            if ("Content-Security-Policy".equalsIgnoreCase(name) && StringUtils.isNotBlank(value)) {
                super.setHeader(name, value.replace("{nonce}", nonce));
            } else {
                super.setHeader(name, value);
            }
        }

        @Override
        public void addHeader(String name, String value) {
            if ("Content-Security-Policy".equalsIgnoreCase(name) && StringUtils.isNotBlank(value)) {
                super.addHeader(name, value.replace("{nonce}", nonce));
            } else {
                super.addHeader(name, value);
            }
        }
    }
}
