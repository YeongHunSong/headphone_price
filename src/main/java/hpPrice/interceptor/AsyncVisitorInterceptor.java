package hpPrice.interceptor;

import hpPrice.common.ip.IpAddressUtil;
import hpPrice.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;


@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncVisitorInterceptor implements AsyncHandlerInterceptor {

    private final VisitorService visitorService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestUrl = request.getRequestURL().toString();
        String query = request.getQueryString();
        if (query != null) requestUrl += "?" + query;
        visitorService.recordVisitor(IpAddressUtil.getClientIp(request), requestUrl);
        return true;
    }


}