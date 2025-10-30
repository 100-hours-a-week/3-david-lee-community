package bootcamp.kakao.community.security.auth.application;

import bootcamp.kakao.community.security.auth.application.dto.AuthHistoryResponse;
import bootcamp.kakao.community.security.auth.application.dto.LoginRequest;
import bootcamp.kakao.community.security.jwt.application.dto.JwtTokenResponse;

import java.util.*;

public interface AuthUseCase {

    /// 로그인
    JwtTokenResponse login(LoginRequest request, String ip, String deviceType);

    /// 로그아웃
    void logout(Long userId, String deviceType, Optional<String> refreshToken, Optional<String> accessToken);

    /// 토큰 재발급
    JwtTokenResponse reissue(Optional<String> refreshToken, String ip, String deviceType);

    /// 나의 로그인 기록 보기
    List<AuthHistoryResponse> getHistory(Long userId);

}
