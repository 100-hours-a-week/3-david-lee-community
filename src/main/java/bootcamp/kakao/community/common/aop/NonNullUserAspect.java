package bootcamp.kakao.community.common.aop;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.CommonErrorCode;
import bootcamp.kakao.community.security.auth.domain.CustomUserDetails;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NonNullUserAspect {

    /// 한번 더 체크하는 AOP
    @Around("@annotation(bootcamp.kakao.community.common.aop.NonNullUser)")
    public Object checkLogin(ProceedingJoinPoint pjp) throws Throwable {

        /// 먼저 인증 객체가 있는지 체크
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        /// 미인증(=null), 익명 토큰, Principal 타입 불일치 케이스 모두 차단
        if (auth == null ||
                !auth.isAuthenticated() ||
                auth instanceof AnonymousAuthenticationToken ||
                !(auth.getPrincipal() instanceof CustomUserDetails)) {

            /// 401 매핑
            throw new CustomException(CommonErrorCode.UNAUTHORIZED);
        }

        return pjp.proceed();
    }

}
