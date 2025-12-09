package bootcamp.kakao.community.common.response.paging;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "[요청][페이징] 페이징 요청 객체")
public record SliceRequest(

        @Schema(description = "마지막 ID", example = "null")
        Long lastId,

        @Schema(description = "조회할 개수", example = "10")
        int offSet) {
}
