package hpPrice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.GET, "/").permitAll()           // /            GET 허용
                        .requestMatchers(HttpMethod.GET, "/dcsff/**").permitAll()   // /dcsff       GET 허용
                        .requestMatchers(HttpMethod.GET, "/drhp/**").permitAll()    // /drhp        GET 허용
                        .requestMatchers(HttpMethod.GET, "/feedback").permitAll()   // /feedback    GET 허용
                        .requestMatchers(HttpMethod.POST, "/feedback").permitAll()  // /feedback    POST 허용

                        .requestMatchers(HttpMethod.GET, "/js/**").permitAll() //       JS 허용
                        .requestMatchers(HttpMethod.GET, "/css/**").permitAll() //      CSS 허용
                        .requestMatchers(HttpMethod.GET, "/static/**").permitAll() //   CSS JS 접근 허용
                        .requestMatchers(HttpMethod.GET, "/error/**").permitAll() //    에러페이지 허용
                        .anyRequest().denyAll() // 그 외 전부 차단
                );

        http
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self';" +
                                        "img-src 'self' data: https://dcimg1.dcinside.co.kr https://dcimg3.dcinside.co.kr " +
                                        "https://dcimg4.dcinside.co.kr https://dcimg1.dcinside.com " +
                                        "https://cafe.pstatic.net ;" +
                                        "style-src 'self' 'unsafe-inline';" +
                                        "script-src 'self' 'unsafe-inline';")));

//        http
//                .requiresChannel(channel -> channel
//                        .anyRequest().requiresSecure()
//                );

        return http.build();
    }
}
