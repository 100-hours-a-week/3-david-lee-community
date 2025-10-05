package bootcamp.kakao.community.platform.images.image.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[요청][이미지] 이미지 업로드 Request", description = "이미지 업로드를 위한 DTO입니다.")
public record ImageRequest(
        @Schema(description = "이미지 파일(base64 인코딩)", example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgA...")
        String file
) {
}
