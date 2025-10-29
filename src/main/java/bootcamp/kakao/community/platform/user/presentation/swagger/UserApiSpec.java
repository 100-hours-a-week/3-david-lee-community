package bootcamp.kakao.community.platform.user.presentation.swagger;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.platform.user.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import bootcamp.kakao.community.security.auth.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "유저 API", description = "유저 API 입니다")
public interface UserApiSpec {


     /// 회원가입 API
    @Operation(
            summary = "회원 가입 API",
            description = "회원가입을 진행합니다."
    )
    ApiResponse<Void> signup(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestBody @Valid SignUpRequest request);

    /// 회원탈퇴 API
    @Operation(
            summary = "회원 탈퇴 API",
            description = "회원 탈퇴를 진행합니다."
    )
    ApiResponse<Void> delete(
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal Long userId);


    /// 이메일 중복 API
    @Operation(
            summary = "이메일 중복 API",
            description = "회원가입시, 이메일이 중복되는지 체크합니다."
    )
    ApiResponse<DuplicateResponse> checkEmail(@RequestParam String email);


    /// 닉네임 중복 API
    @Operation(
            summary = "닉네임 중복 API",
            description = "회원가입시, 닉네임이 중복되는지 체크합니다."
    )
    ApiResponse<DuplicateResponse> checkNickName(@RequestParam String nickName);

    /// 개인정보 상세 조회
    @Operation(
            summary = "개인정보 조회 API",
            description = "나의 정보를 조회합니다."
    )
    ApiResponse<MyPageResponse> getUser(@AuthenticationPrincipal Long userId);

    /// 다른 유저 정보 조회
    @Operation(
            summary = "다른 유저 조회 API",
            description = "다른 유저의 정보를 조회합니다."
    )
    ApiResponse<UserResponse> getOtherUser(@PathVariable Long userId);

    /// 개인정보 수정
    @Operation(
            summary = "개인정보 수정 API",
            description = "나의 정보를 수정합니다."
    )
    ApiResponse<Void> updateUser(@RequestBody @Valid UserUpdateRequest request,
                                 @AuthenticationPrincipal Long userId);

    /// 비밀번호 변경
    @Operation(
            summary = "비밀번호 수정 API",
            description = "기존 비밀번호를 바탕으로 비밀번호 수정합니다."
    )
    ApiResponse<Void> updatePassword(@RequestBody @Valid PwUpdateRequest pwReq,
                                     @AuthenticationPrincipal Long userId);


}
