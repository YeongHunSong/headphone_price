package hpPrice.config;

import hpPrice.interceptor.AsyncVisitorInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AsyncVisitorInterceptor asyncVisitorInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(asyncVisitorInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**");
    }

}
