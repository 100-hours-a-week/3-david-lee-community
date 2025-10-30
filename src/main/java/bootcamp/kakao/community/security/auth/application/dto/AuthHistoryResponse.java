package bootcamp.kakao.community.security.auth.application.dto;

import bootcamp.kakao.community.security.auth.domain.AuthHistory;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AuthHistoryResponse(
        String ip,
        String device,
        String status,
        LocalDateTime date
) {

    /// 정적 팩토리 메서드
    public static AuthHistoryResponse from(AuthHistory authHistory) {
        return AuthHistoryResponse.builder()
                .ip(authHistory.getIpAddress())
                .device(authHistory.getDevice())
                .status(authHistory.getStatus().getLabel())
                .date(authHistory.getCreatedDate())
                .build();
    }

    /// 정적 팩토리 메서드
    public static List<AuthHistoryResponse> from(List<AuthHistory> authHistories) {
        return authHistories.stream()
                .map(AuthHistoryResponse::from)
                .toList();
    }

}
