package bootcamp.kakao.community.platform.images.image.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[요청][이미지] 이미지 업로드 Request", description = "이미지 업로드를 위한 DTO입니다.")
public record PreSignedImageRequest(

        @Schema(description = "저장할 이미지 파일 이름", example = "file.jpg")
        String fileName
) {
}
