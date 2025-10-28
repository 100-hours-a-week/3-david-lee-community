package bootcamp.kakao.community.common.aop;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.CommonErrorCode;
import bootcamp.kakao.community.common.util.HttpUtil;
import bootcamp.kakao.community.security.session.application.SessionProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

@Component
@Aspect
@RequiredArgsConstructor
public class HttpSessionIdAspect {

    private final SessionProvider sessionProvider;
    private final HttpServletRequest request;
    private final HttpUtil httpUtil;

    @Around("execution(* *(.., @bootcamp.kakao.community.common.aop.HttpSessionId (*), ..))")
    public Object injectUserId(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation instanceof HttpSessionId) {
                    Optional<String> sessionId = httpUtil.getSessionId(request);
                    Long userId = sessionProvider.getUserBySession(sessionId);
                    if (userId == null) {
                        throw new CustomException(CommonErrorCode.UNAUTHORIZED);
                    }
                    args[i] = userId; // 파라미터에 유저 ID 주입
                }
            }
        }

        return joinPoint.proceed(args);
    }

}

