package bootcamp.kakao.community.security.auth.annotation;

import bootcamp.kakao.community.platform.user.domain.entity.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auth {

    /// 기존 UserRole 으로 설정
    UserRole role() default UserRole.MEMBER;


}
