package bootcamp.kakao.community.security.jwt.application.dto;

import bootcamp.kakao.community.platform.user.domain.entity.User;
import bootcamp.kakao.community.platform.user.domain.entity.UserRole;
import lombok.Builder;

@Builder
public record JwtTokenRequest(
        Long userId,
        UserRole role,
        String ip,
        String deviceType
) {

    /// 정적 팩토리 메서드
    public static JwtTokenRequest from(User user, String ip, String deviceType) {
        return JwtTokenRequest.builder()
                .userId(user.getId())
                .role(user.getRole())
                .deviceType(deviceType)
                .ip(ip)
                .build();
    }

}
