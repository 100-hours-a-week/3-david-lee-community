package bootcamp.kakao.community.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
/**
 * CORS 설정을 위한 Config 입니다.
 */
@Configuration
public class CorsConfig {

    @Value("${cors.front.host}")
    private String frontHost;

    @Value("${cors.back.host}")
    private String backHost;

    /// CORS Bean 설정하기
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        /// CORS 주소 추가
        configuration.setAllowedOriginPatterns(List.of(frontHost, backHost));

        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addExposedHeader("Authorization");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
