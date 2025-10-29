package bootcamp.kakao.community.security.session.interceptor;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.CommonErrorCode;
import bootcamp.kakao.community.common.response.code.SecurityErrorCode;
import bootcamp.kakao.community.common.response.code.UserErrorCode;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import bootcamp.kakao.community.platform.user.domain.entity.UserRole;
import bootcamp.kakao.community.platform.user.domain.repository.UserRepository;
import bootcamp.kakao.community.security.auth.annotation.Auth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 서블릿 컨텍스트에서 1차 인증이 된 요청을
 * 2차로 권한에 따른 역할을 수정하는 기능을 합니다.
 */

/// 인가를 위한 인터셉터
@Component
@RequiredArgsConstructor
public class SessionAuthInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    /// 역할에 따른 인가 수행
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
            /// 어노티에션이 없는 컨트롤러는 모두 접근이 가능한 것
            return true;
        }

        /// 인증된 HTTP 에서 유저ID 조회
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            /// 없으면 null 이기에 인증 필요
            throw new CustomException(CommonErrorCode.UNAUTHORIZED);
        }

        /// 유저 ID 바탕으로 실제 권한 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_USER));
        UserRole role = user.getRole();

        /// 최소권한이 멤버
        if (auth.role().equals(UserRole.MEMBER)) {

            /// 비회원만 false, ADMIN true
            if (role == null) {
                return false;
            }
            return true;
        }

        /// 최소권한이 어드민
        else if (auth.role().equals(UserRole.ADMIN)) {

            /// 비회원이거나 Member인 경우 false
            if (role == null || user.getRole().equals(UserRole.MEMBER)) {
                return false;
            }
            return true;
        }

        else {
            /// 나머지 ?
            return false;
        }
    }
}
