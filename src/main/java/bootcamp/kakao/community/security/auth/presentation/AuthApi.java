package bootcamp.kakao.community.security.auth.presentation;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.platform.user.domain.entity.UserRole;
import bootcamp.kakao.community.security.auth.annotation.Auth;
import bootcamp.kakao.community.security.auth.application.AuthUseCase;
import bootcamp.kakao.community.security.auth.presentation.swagger.AuthApiSpec;
import bootcamp.kakao.community.common.util.HttpUtil;
import bootcamp.kakao.community.security.auth.application.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

        /// 서비스 로직 실행
        String sessionId = service.login(request);

        /// 쿠키로 세션 키 전송하기
        httpUtil.addSessionId(httpServletResponse, sessionId);

        /// 리턴
        return ApiResponse.created();
    }

    /**
     * 로그아웃
     */
    @Auth(role = UserRole.MEMBER)
    @DeleteMapping
    public ApiResponse<Void> logout(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        /// 기존 세션이 존재하는지 체크
        Optional<String> sessionId = httpUtil.getSessionId(httpServletRequest);

        /// 서비스 로직 실행
        service.logout(sessionId);

        /// 세션키 쿠키 삭제하기
        httpUtil.removeSessionId(httpServletResponse);

        /// 리턴
        return ApiResponse.deleted();
    }
}
