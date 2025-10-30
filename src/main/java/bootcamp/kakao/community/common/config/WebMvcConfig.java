package bootcamp.kakao.community.common.config;

import bootcamp.kakao.community.security.auth.annotation.CurrentUserArgResolver;
import bootcamp.kakao.community.security.jwt.interceptor.JwtAuthorizationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    /// 인터셉터 추가
    private final JwtAuthorizationInterceptor jwtAuthorizationInterceptor;

    /// CurrentId 리졸버 추가
    private final CurrentUserArgResolver currentUserArgResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthorizationInterceptor);
    }

    /// 새로 추가하기
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgResolver);
    }
}
