package bootcamp.kakao.community.security.config;

import bootcamp.kakao.community.security.jwt.filter.JwtDeniedHandler;
import bootcamp.kakao.community.security.jwt.filter.JwtFailureHandler;
import bootcamp.kakao.community.security.jwt.filter.JwtFilter;
import bootcamp.kakao.community.security.jwt.filter.RequestMatcherHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import static bootcamp.kakao.community.platform.user.domain.entity.UserRole.ADMIN;
import static bootcamp.kakao.community.platform.user.domain.entity.UserRole.MEMBER;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtFailureHandler jwtFailureHandler;
    private final JwtDeniedHandler jwtDeniedHandler;

    private final RequestMatcherHolder requestMatcherHolder;
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * SecurityFilterChain 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(requestMatcherHolder.getRequestMatchersByMinRole(null))
                        .permitAll()
                        .requestMatchers(requestMatcherHolder.getRequestMatchersByMinRole(MEMBER))
                        .hasAnyAuthority(ADMIN.getRole(), MEMBER.getRole())
                        .requestMatchers(requestMatcherHolder.getRequestMatchersByMinRole(ADMIN))
                        .hasAnyAuthority(ADMIN.getRole())
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) /// UsernamePasswordAuthenticationFilter.class 전에 실행하도록
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(jwtFailureHandler)
                            .accessDeniedHandler(jwtDeniedHandler);
                })
                .build();
    }

    /**
     * 비밀번호 암호화를 위한 BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


}

