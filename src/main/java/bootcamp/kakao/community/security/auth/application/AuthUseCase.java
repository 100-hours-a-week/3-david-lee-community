package bootcamp.kakao.community.security.auth.application;

import bootcamp.kakao.community.security.auth.application.dto.LoginRequest;

import java.util.Optional;

public interface AuthUseCase {

    /// 로그인
    String login(LoginRequest request);

    /// 로그아웃
    void logout(Optional<String> sessionId);


}
