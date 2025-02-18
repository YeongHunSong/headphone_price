package hpPrice.config;

import hpPrice.interceptor.AsyncVisitorInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AsyncVisitorInterceptor asyncVisitorInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(asyncVisitorInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**",
                        "/site.webmanifest", "/favicon.ico", "/apple-touch-icon.png",
                        "/favicon-16x16.png", "/favicon-32x32.png",
                        "/android-chrome-192x192.png", "/android-chrome-512x512.png"
                );
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/site.webmanifest", "/favicon.ico",
                        "/favicon-16x16.png", "/favicon-32x32.png", "/apple-touch-icon.png",
                        "/android-chrome-192x192.png", "/android-chrome-512x512.png")
                .addResourceLocations("classpath:/static/favicon/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(30)));
    }

//    @Bean
//    public ServletWebServerFactory servletContainer() {
//        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint securityConstraint = new SecurityConstraint();
//                securityConstraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection = new SecurityCollection();
//                collection.addPattern("/*");
//                securityConstraint.addCollection(collection);
//                context.addConstraint(securityConstraint);
//            }
//        };
//        tomcat.addAdditionalTomcatConnectors(redirectConnector());
//        return tomcat;
//    }
//
//    private Connector redirectConnector() {
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        connector.setScheme("http");
//        connector.setPort(80);
//        connector.setSecure(false);
//        connector.setRedirectPort(443);
//        return connector;
//    }
}
