package bootcamp.kakao.community.platform.user.application.dto;

import bootcamp.kakao.community.common.util.ImageUtil;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(
        name = "[응답][유저] 마이페이지 정보 Response",
        description = "마이페이지에서 사용자 정보를 응답할 때 사용하는 DTO입니다."
)
@Builder
public record MyPageResponse(
        @Schema(description = "유저 식별자", example = "1")
        Long userId,
        @Schema(description = "이름", example = "이도연")
        String name,
        @Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/profile/10001.png")
        String imageUrl,
        @Schema(description = "닉네임", example = "david/클라우드")
        String nickname,
        @Schema(description = "이메일", example = "david@example.com")
        String email,
        @Schema(description = "권한(역할)", example = "유저")
        String role
) {

    /// 정적 팩토리 메서드
    public static MyPageResponse from(User user) {
        return MyPageResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .imageUrl(ImageUtil.getUrlByKey(user.getImageKey()))
                .nickname(user.getNickname())
                .email(user.getEmail())
                .role(user.getRole().getLabel())
                .build();
    }
}
