package bootcamp.kakao.community.security.auth.annotation;

import io.swagger.v3.oas.annotations.Hidden;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 커스텀 어노테이션을 제작해서 가져오기
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Hidden
public @interface CurrentUserId {
}
