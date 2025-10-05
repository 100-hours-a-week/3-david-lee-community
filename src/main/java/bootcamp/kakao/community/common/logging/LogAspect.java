package bootcamp.kakao.community.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("execution(* bootcamp.kakao.community.platform..*Service.*(..))")
    private void applicationLayer() {
    }

    @Around("applicationLayer()")
    public Object logProcessTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();  // 실제 메서드 실행

        long executionTime = System.currentTimeMillis() - start;

        /// 서비스명 추출하기
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
        String[] split = declaringTypeName.split("\\.");
        String serviceName = split[split.length - 1];

        log.info("[서비스 로깅] 메서드 소요 시간: {}.{} = {}ms",
                serviceName,
                joinPoint.getSignature().getName(),
                executionTime);

        return proceed;
    }

}
