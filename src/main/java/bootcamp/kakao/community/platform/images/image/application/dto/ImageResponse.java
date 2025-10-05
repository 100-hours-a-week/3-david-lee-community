package bootcamp.kakao.community.platform.images.image.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(
        name = "[응답][이미지] 이미지 응답 Response",
        description = "S3에 저장된 이미지 정보를 응답하기 위한 DTO입니다."
)
@Builder
public record ImageResponse(
        @Schema(description = "원본 파일 이름", example = "example-image.png")
        String fileName,

        @Schema(description = "S3 저장 주소 (Pre-Signed URL)", example = "https://s3.amazonaws.com/bucket/example-image.png?X-Amz-Signature=...")
        String preSignedUrl) {

    /// 정적 팩토리 메서드
    public static ImageResponse from(String fileName, String preSignedUrl) {
        return ImageResponse.builder()
                .fileName(fileName)
                .preSignedUrl(preSignedUrl)
                .build();
    }
}
