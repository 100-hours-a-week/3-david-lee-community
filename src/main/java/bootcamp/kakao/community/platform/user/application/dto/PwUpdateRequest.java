package bootcamp.kakao.community.platform.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(name = "[요청][유저] 비밀번호 변경 요청 Request", description = "사용자의 비밀번호 변경 요청을 위한 DTO입니다.")
public record PwUpdateRequest(

        @Schema(description = "기존 비밀번호", example = "oldPass123!")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "비밀번호는 8~20자, 대문자·소문자·숫자·특수문자를 각각 최소 1개 이상 포함해야 합니다."
        )
        String oldPassword,

        @Schema(description = "새 비밀번호", example = "newPass456!")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "비밀번호는 8~20자, 대문자·소문자·숫자·특수문자를 각각 최소 1개 이상 포함해야 합니다."
        )
        String newPassword,


        @Schema(description = "새 비밀번호 확인", example = "newPass456!")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "비밀번호는 8~20자, 대문자·소문자·숫자·특수문자를 각각 최소 1개 이상 포함해야 합니다."
        )
        String confirmPassword
) {
}
