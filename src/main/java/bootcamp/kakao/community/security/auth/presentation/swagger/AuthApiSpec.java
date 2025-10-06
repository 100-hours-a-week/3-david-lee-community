package bootcamp.kakao.community.security.auth.presentation.swagger;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.security.auth.application.dto.LoginRequest;
import bootcamp.kakao.community.security.auth.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "인증 API", description = "로그인/로그아웃/재발급를 수행하는 API입니다")
public interface AuthApiSpec {

    @Operation(
            summary = "로그인 API",
            description = "이메일/비밀번호를 통해 로그인하는 API"
    )
    ApiResponse<Void> login(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestBody @Valid LoginRequest request);


    @Operation(
            summary = "로그아웃 API",
            description = "이미 인증된 유저가 로그아웃하는 API"
    )
    ApiResponse<Void> logout(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(
            summary = "액세스토큰 재발급 API",
            description = "액세스 토큰을 재발급받는 API"
    )
    ApiResponse<Void> reissue(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    );
}
