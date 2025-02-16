package hpPrice.config;


import jakarta.servlet.Filter;
import org.apache.catalina.filters.RateLimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.GET, "/").permitAll()           // /            GET 허용
                        .requestMatchers(HttpMethod.GET, "/drhp/**").permitAll()    // /drhp        GET 허용
                        .requestMatchers(HttpMethod.GET, "/dcsff/**").permitAll()   // /dcsff       GET 허용
                        .requestMatchers(HttpMethod.GET, "/feedback").permitAll()   // /feedback    GET 허용
                        .requestMatchers(HttpMethod.POST, "/feedback").permitAll()  // /feedback    POST 허용

                        .requestMatchers(HttpMethod.GET, "/js/**").permitAll() //           JS 허용
                        .requestMatchers(HttpMethod.GET, "/css/**").permitAll() //          CSS 허용
                        .requestMatchers(HttpMethod.GET, "/error/**").permitAll() //        에러페이지 허용
                        .requestMatchers(HttpMethod.GET, "/favicon/**").permitAll() //      favicon 허용
                        .requestMatchers(HttpMethod.GET, "/favicon.ico").permitAll() //     favicon 허용
                        .anyRequest().denyAll() // 그 외 전부 차단
                );

        http
                .headers(headers -> headers
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))

                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self';" +
                                        "img-src 'self' data: https://dcimg1.dcinside.co.kr https://dcimg3.dcinside.co.kr " +
                                        "https://dcimg4.dcinside.co.kr https://dcimg1.dcinside.com " +
                                        "https://cafe.pstatic.net ;" +
                                        "style-src 'self' 'unsafe-inline';" +
                                        "script-src 'self' 'unsafe-inline';"))

                        .permissionsPolicy(permissions -> permissions
                                .policy("display-capture=(self), picture-in-picture=(self), " +
                                        "accelerometer=(), autoplay=(), camera=(), document-domain=(), encrypted-media=(), " +
                                        "fullscreen=(), geolocation=(), gyroscope=(), magnetometer=(), " +
                                        "microphone=(), midi=(), payment=(), publickey-credentials-get=(), " +
                                        "screen-wake-lock=(), sync-xhr=(), usb=(), web-share=(), xr-spatial-tracking=()"))
                );

//        http
//                .requiresChannel(channel -> channel
//                        .anyRequest().requiresSecure()
//                );

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<Filter> rateLimitFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
