package bootcamp.kakao.community.platform.batch;

import bootcamp.kakao.community.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/v1/test/batch")
@RequiredArgsConstructor
public class BatchApi implements BatchApiSpec{

    private final PostBatchService postBatchService;
    private final ImageBatchService imageBatchService;

    /**
     * 이미지 삭제 배치 프로세스
     */
    @PostMapping("/images")
    public ApiResponse<Void> batchImages() throws IOException {

        /// 서비스
        imageBatchService.deleteBatchEveryDay();

        /// 리턴
        return ApiResponse.created();
    }

    /**
     * 게시글 매핑 프로세스
     */
    @PostMapping("/post")
    public ApiResponse<Void> batchPost() {

        /// 서비스
        postBatchService.syncPostStatsToDB();

        /// 리턴
        return ApiResponse.created();
    }
}
