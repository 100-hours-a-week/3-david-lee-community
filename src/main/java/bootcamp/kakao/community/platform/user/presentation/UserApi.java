package bootcamp.kakao.community.platform.user.presentation;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.platform.user.application.UserUseCase;
import bootcamp.kakao.community.platform.user.application.dto.*;
import bootcamp.kakao.community.platform.user.presentation.swagger.UserApiSpec;
import bootcamp.kakao.community.common.util.HttpUtil;
import bootcamp.kakao.community.security.jwt.application.dto.JwtTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import bootcamp.kakao.community.security.auth.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserApi implements UserApiSpec {

    /// 유저 서비스
    private final UserUseCase service;

    /// 토큰 발급 서비스
    private final HttpUtil httpUtil;

    /**
     * RestAPI 일관성을 위해
     * 회원가입 엔드포인트는 "/v1/users" 로
     */
    @PostMapping
    public ApiResponse<Void> signup(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestBody @Valid SignUpRequest request) {

        /// 디바이스 조회
        var requestType = httpUtil.getRequestInfo(httpServletRequest);

        /// 회원가입 서비스 로직
        JwtTokenResponse response = service.signUp(request, requestType.ip(), requestType.deviceType());

        /// 성공했다면, 토큰 발급
        httpUtil.addAccessTokenHeader(httpServletResponse, response.accessToken());
        httpUtil.addRefreshTokenCookie(httpServletResponse, response.refreshToken());

        /// 리턴
        return ApiResponse.created();
    }

    /// 유저 소프트 삭제
    @PutMapping
    public ApiResponse<Void> delete(
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal Long userId) {

        /// 서비스 로직
        service.withdraw(userId);

        /// 토큰 삭제
        httpUtil.removeRefreshTokenCookie(httpServletResponse);

        /// 응답
        return ApiResponse.deleted();
    }

    /// 이메일 중복 체크
    @GetMapping("/email")
    public ApiResponse<DuplicateResponse> checkEmail(@RequestParam String email) {

        /// 서비스 로직
        boolean duplicateEmail = service.checkDuplicateEmail(email);

        /// 리턴
        return ApiResponse.ok(DuplicateResponse.of(duplicateEmail));
    }

    /// 닉네임 중복 체크
    @GetMapping("/nickname")
    public ApiResponse<DuplicateResponse> checkNickName(@RequestParam String nickName) {

        /// 서비스 로직
        boolean duplicateNickName = service.checkDuplicateNickName(nickName);

        /// 리턴
        return ApiResponse.ok(DuplicateResponse.of(duplicateNickName));
    }

    /// 나의 정보 조회하기
    @GetMapping("/mypage")
    public ApiResponse<MyPageResponse> getUser(
            @AuthenticationPrincipal Long userId) {

        /// 서비스 로직
        MyPageResponse user = service.getUser(userId);

        /// 리턴
        return ApiResponse.ok(user);
    }

    /// 다른 유저 정보 조회하기
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getOtherUser(@PathVariable Long userId) {

        /// 서비스 로직
        UserResponse user = service.getOtherUser(userId);

        /// 리턴
        return ApiResponse.ok(user);
    }

    /// 나의 정보 수정하기
    @PutMapping("/mypage")
    public ApiResponse<Void> updateUser(@RequestBody @Valid UserUpdateRequest request,
                                        @AuthenticationPrincipal Long userId) {

        /// 서비스 로직
        service.updateUser(request, userId);

        /// 리턴
        return ApiResponse.updated();
    }

    /// 비밀번호 변경
    @PutMapping("/password")
    public ApiResponse<Void> updatePassword(@RequestBody @Valid PwUpdateRequest pwReq,
                                            @AuthenticationPrincipal Long userId) {

        /// 서비스 로직
        service.updatePassword(userId, pwReq);

        /// 리턴
        return ApiResponse.updated();
    }

}
