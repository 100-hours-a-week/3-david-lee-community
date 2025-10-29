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

    private final HttpServletRequest request;

    @Around("execution(* *(.., @bootcamp.kakao.community.common.aop.HttpSessionId (*), ..))")
    public Object injectUserId(ProceedingJoinPoint joinPoint) throws Throwable {

        /// 메서드 시그니처 가져오기
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        /// 방법 및 인자 받기
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {

                /// 어노테이션에서만 사용
                if (annotation instanceof HttpSessionId) {

                    /// HTTP 유저 ID 추출
                    Long userId = (Long) request.getAttribute("userId");

                    args[i] = userId;
                }
            }
        }

        /// 계속해서 진행
        return joinPoint.proceed(args);
    }

}

