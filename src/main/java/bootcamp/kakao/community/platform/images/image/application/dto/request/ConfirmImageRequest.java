package bootcamp.kakao.community.platform.images.image.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "[요청][이미지] 이미지 확정 Request", description = "이미지 업로드 이후, 확정하기 위한 DTO입니다.")
public record ConfirmImageRequest(

        @Schema(description = "이미지 파일 키 값", example = "[\"6cab0f56-4644-4ebf-b076-0eb76fe2a7ec.jpeg\", \"6cab0f56-4644-4ebf-b076-0eb76fe2a7ec.jpeg\"]")
        List<String> keys

) {

}
