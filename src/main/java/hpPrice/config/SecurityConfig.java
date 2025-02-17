package hpPrice.config;


import hpPrice.filter.CSPNonceFilter;
import jakarta.servlet.Filter;
import org.apache.catalina.filters.RateLimitFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.GET, "/", "/drhp/**", "/dcsff/**", "/feedback", "/error").permitAll() // GET
                        .requestMatchers(HttpMethod.POST, "/feedback").permitAll() // feedback POST

                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // staticResources
                        .requestMatchers(HttpMethod.GET, "/error/**").permitAll() // resources GET
                        .requestMatchers(HttpMethod.GET,  "/favicon.ico", "/favicon-16x16.png", "/favicon-32x32.png",
                                "/apple-touch-icon.png", "/site.webmanifest", "/android-chrome-192x192.png", "/android-chrome-512x512.png").permitAll() // favicon GET
                        .anyRequest().denyAll()) // 그 외 전부 차단

                .headers(headers -> headers
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))

                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self';" +
                                        "img-src 'self' data: https://dcimg1.dcinside.co.kr https://dcimg3.dcinside.co.kr " +
                                        "https://dcimg4.dcinside.co.kr https://dcimg1.dcinside.com " +
                                        "https://cafe.pstatic.net ;" +
                                        "style-src 'self' 'nonce-{nonce}';" +
                                        "script-src 'nonce-{nonce}' 'strict-dynamic';"))

                                .permissionsPolicy(permissions -> permissions
                                        .policy(
                                                "display-capture=(), picture-in-picture=(self), fullscreen=(self), " +
                                                "accelerometer=(), autoplay=(), camera=(), encrypted-media=(), " +
                                                "geolocation=(), gyroscope=(), magnetometer=(), " +
                                                "microphone=(), midi=(), payment=(), publickey-credentials-get=(), " +
                                                "screen-wake-lock=(), sync-xhr=(), usb=(), web-share=(), xr-spatial-tracking=()"
                                        )))

                .addFilterBefore(new CSPNonceFilter(), HeaderWriterFilter.class)

//                .requiresChannel(channel -> channel.anyRequest().requiresSecure())
        ;

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
