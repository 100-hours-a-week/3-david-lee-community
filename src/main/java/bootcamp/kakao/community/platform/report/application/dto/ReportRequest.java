package bootcamp.kakao.community.platform.report.application.dto;

import bootcamp.kakao.community.platform.report.domain.entity.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[요청][신고] 신고 요청 Request", description = "신고를 위한 요청 DTO입니다.")
public record ReportRequest(
        @Schema(description = "신고 유형", example = "유저")
        ReportType type,

        @Schema(description = "신고 대상 콘텐츠 ID", example = "1")
        Long contentId,

        @Schema(description = "신고 사유", example = "부적절한 내용 포함")
        String reason
) {

}
