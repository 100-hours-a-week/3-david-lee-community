package bootcamp.kakao.community.common.aop;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.CommonErrorCode;
import bootcamp.kakao.community.security.session.application.SessionProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class HttpSessionIdAspect {

    private final SessionProvider sessionProvider;

    /// HttpSession 가져오는 AOP
    @Around("@annotation(bootcamp.kakao.community.common.aop.HttpSessionId)")
    public Object getUserByCookie(ProceedingJoinPoint pjp) throws Throwable {

        /// 해당 세션 ID를 가진 유저가 실제로 존재 하는지 체크
        Long userId = sessionProvider.getUserBySession(pjp.getArgs()[0].toString());

        /// 없으면 예외처리
        if (userId == null) {

            /// 401 매핑
            throw new CustomException(CommonErrorCode.UNAUTHORIZED);
        }
        return pjp.proceed();}

}

