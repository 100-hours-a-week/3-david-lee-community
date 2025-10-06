package bootcamp.kakao.community.platform.batch;

import bootcamp.kakao.community.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;

@Tag(name = "테스트용 배치 API", description = "작동여부 파악을 위한 테스트용, 배치 API 입니다")
public interface BatchApiSpec {

    @Operation(
            summary = "이미지 삭제 배치 API",
            description = "S3에서 사용하지 않는 이미지를 삭제합니다."
    )
    ApiResponse<Void> batchImages() throws IOException;


    @Operation(
            summary = "게시글 매핑 API",
            description = "레디스의 값을 DB에 매핑합니다."
    )
    ApiResponse<Void> batchPost();

}
