package bootcamp.kakao.community.security.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[요청][인증] 로그인 Request", description = "로그인 요청을 위한 DTO입니다.")
public record LoginRequest(
        @Schema(description = "사용자 이메일", example = "ktbcloud@kakao.com")
        String email,

        @Schema(description = "사용자 비밀번호", example = "Hello123!World")
        String password
) {
}
