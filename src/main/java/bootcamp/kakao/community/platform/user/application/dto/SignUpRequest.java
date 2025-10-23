package bootcamp.kakao.community.platform.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
@Schema(name = "[요청][유저] 회원가입 Request", description = "회원가입을 위한 DTO입니다.")
public record SignUpRequest(

        @Schema(example = "이도연")
        String name,

        @Schema(example = "null")
        String imageKey,

        @NotBlank(message = "닉네임을 입력해주세요. (띄어쓰기 불가)")
        @Size(max = 10, message = "닉네임은 10글자 이내입니다.")
        @Schema(example = "david")
        String nickname,

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 주소 형식을 입력해주세요.")
        @Schema(example = "ktbcloud@kakao.com")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "비밀번호는 8~20자, 대문자·소문자·숫자·특수문자를 각각 최소 1개 이상 포함해야 합니다."
        )
        @Schema(example = "Hello123!World")
        String password,

        @NotBlank(message = "비밀번호 확인을 입력해주세요.")
        @Schema(example = "Hello123!World")
        String confirmPassword
) {

}

