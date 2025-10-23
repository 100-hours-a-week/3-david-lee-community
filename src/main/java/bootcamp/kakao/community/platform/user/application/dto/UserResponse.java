package bootcamp.kakao.community.platform.user.application.dto;

import bootcamp.kakao.community.common.util.ImageUtil;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(
        name = "[응답][유저] 사용자 정보 Response",
        description = "사용자 기본 정보를 반환할 때 사용하는 DTO입니다."
)
@Builder
public record UserResponse(
        @Schema(description = "유저 식별자", example = "1")
        Long userId,
        @Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/profile/1.png")
        String imageUrl,
        @Schema(description = "닉네임", example = "이도연")
        String nickname
) {

    /// 정적 팩토리 메서드
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .imageUrl(ImageUtil.getUrlByKey(user.getImageKey()))
                .nickname(user.getNickname())
                .build();
    }
}
