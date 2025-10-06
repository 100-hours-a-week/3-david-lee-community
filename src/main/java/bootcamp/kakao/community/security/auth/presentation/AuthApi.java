package bootcamp.kakao.community.security.auth.presentation;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.security.auth.presentation.swagger.AuthApiSpec;
import bootcamp.kakao.community.security.jwt.application.HttpUtil;
import bootcamp.kakao.community.security.auth.application.AuthUseCase;
import bootcamp.kakao.community.security.auth.application.dto.LoginRequest;
import bootcamp.kakao.community.security.auth.domain.CustomUserDetails;
import bootcamp.kakao.community.security.jwt.application.dto.JwtTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthApi implements AuthApiSpec {

    private final AuthUseCase service;

    /// HTTP 서비스
    private final HttpUtil httpUtil;

    // =================
    //  퍼블릭 로직
    // =================

    /**
     * 로그인
     */
    @PostMapping
    public ApiResponse<Void> login(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestBody @Valid LoginRequest request) {

        /// 디바이스 조회
        String deviceType = httpUtil.getDeviceType(httpServletRequest);

        /// 서비스 로직 실행
        JwtTokenResponse response = service.login(request, deviceType);

        /// 쿠키로 전송하기
        httpUtil.addAccessTokenCookie(httpServletResponse, response.accessToken());
        httpUtil.addRefreshTokenCookie(httpServletResponse, response.refreshToken());

        /// 리턴
        return ApiResponse.created();
    }

    /**
     * 로그아웃
     */
    @DeleteMapping
    public ApiResponse<Void> logout(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        /// 디바이스 조회
        String deviceType = httpUtil.getDeviceType(httpServletRequest);

        /// 리프레쉬 토큰 까보기
        Optional<String> refreshToken = httpUtil.getRefreshToken(httpServletRequest);

        /// 서비스 로직 실행
        service.logout(customUserDetails.getId(), deviceType, refreshToken);

        /// 쿠키 삭제하기
        httpUtil.removeAccessTokenCookie(httpServletResponse);
        httpUtil.removeRefreshTokenCookie(httpServletResponse);

        /// 리턴
        return ApiResponse.deleted();
    }

    /**
     * 토큰 재발급
     */
    @PutMapping
    public ApiResponse<Void> reissue(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        /// 디바이스 조회
        String deviceType = httpUtil.getDeviceType(httpServletRequest);

        /// 리프레쉬 토큰 까보기
        Optional<String> refreshToken = httpUtil.getRefreshToken(httpServletRequest);

        /// 서비스 로직 실행
        JwtTokenResponse response = service.reissue(deviceType, refreshToken);

        /// 액세스 쿠키로 전송하기
        httpUtil.addAccessTokenCookie(httpServletResponse, response.accessToken());

        /// 리턴
        return ApiResponse.updated();
    }

}
