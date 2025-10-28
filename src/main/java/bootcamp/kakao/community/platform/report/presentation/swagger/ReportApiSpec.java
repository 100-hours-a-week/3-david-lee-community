package bootcamp.kakao.community.platform.report.presentation.swagger;

import bootcamp.kakao.community.common.aop.HttpSessionId;
import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.platform.report.application.dto.ReportRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "신고 API", description = "유저/게시글/댓글 신고하는 API")
public interface ReportApiSpec {

    @Operation(
            summary = "신고 API",
            description = "회원만 유저/게시글/댓글 신고하는 API"
    )
    ApiResponse<Void> report(@RequestBody @Valid ReportRequest request,
                             @HttpSessionId Long userId);

}
