package hpPrice.interceptor;

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
        visitorService.recordVisitor(request.getRemoteAddr(), requestUrl);
        return true;
    }

//    @Override
//    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) {
////        비동기 요청 처리가 시작된 후 호출
////        추가적인 비동기 작업 수행 가능
//    }

}
