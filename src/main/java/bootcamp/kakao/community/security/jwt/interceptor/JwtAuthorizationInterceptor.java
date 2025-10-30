package bootcamp.kakao.community.security.jwt.interceptor;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.CommonErrorCode;
import bootcamp.kakao.community.platform.user.domain.entity.UserRole;
import bootcamp.kakao.community.security.auth.annotation.Auth;
import bootcamp.kakao.community.security.jwt.application.JwtValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 인가용 핸들러
 * 이미 서블릿 컨텍스트에서 인증을 받은 유저만 오기 때문에,
 */
@Component
@RequiredArgsConstructor
public class JwtAuthorizationInterceptor implements HandlerInterceptor {

    /// @Auth 어노테이션에 적힌 내용을 바탕으로 권한별 컨트롤러 사용
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        /// 어노테이션 가져오기
        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
        if (auth == null) {
            /// 어노테이션이 없는 컨트롤러는 모두 허용
            return true;
        }

        // <! -- 여기부터는 인가가 필요한 메서드 -->

        /// 인증된 HTTP 에서 유저ID, Role 조회
        Long userId = (Long) request.getAttribute("userId");
        UserRole role = (UserRole) request.getAttribute("role");
        if (userId == null || role == null) {
            /// 없으면 null 이기에 인증 필요
            throw new CustomException(CommonErrorCode.UNAUTHORIZED);
        }

        /// 최소권한이 멤버
        if (auth.role().equals(UserRole.MEMBER)) {
            /// member 이거나 admin이면 true
            return true;
        }

        /// 최소권한이 어드민
        else if (auth.role().equals(UserRole.ADMIN)) {

            /// 비회원이거나 Member인 경우 false
            if (role.equals(UserRole.MEMBER)) {
                throw new CustomException(CommonErrorCode.FORBIDDEN);
            }
            return true;
        }

        else {
            /// 나머지 에외
            throw new CustomException(CommonErrorCode.FORBIDDEN);
        }

    }
}
