package bootcamp.kakao.community.common.config;

import bootcamp.kakao.community.security.auth.annotation.CurrentUserArgResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    /// CurrentId 리졸버 추가
    private final CurrentUserArgResolver currentUserArgResolver;

    /// 새로 추가하기
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgResolver);
    }
}
