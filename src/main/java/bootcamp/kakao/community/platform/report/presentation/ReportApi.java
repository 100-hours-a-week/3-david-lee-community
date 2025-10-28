package bootcamp.kakao.community.platform.report.presentation;

import bootcamp.kakao.community.common.aop.HttpSessionId;
import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.platform.report.application.ReportUseCase;
import bootcamp.kakao.community.platform.report.application.dto.ReportRequest;
import bootcamp.kakao.community.platform.report.presentation.swagger.ReportApiSpec;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/report")
@RequiredArgsConstructor
public class ReportApi implements ReportApiSpec {

    private final ReportUseCase service;

    @PostMapping()
    public ApiResponse<Void> report(@RequestBody @Valid ReportRequest request,
                                    @HttpSessionId Long userId) {

        /// 서비스
        service.createReport(request, userId);

        /// 리턴
        return ApiResponse.created();
    }
}
