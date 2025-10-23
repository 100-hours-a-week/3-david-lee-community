package bootcamp.kakao.community.platform.images.image.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(
        name = "[응답][이미지] 이미지 응답 Response",
        description = "PreSignedURL 응답하기 위한 DTO입니다."
)
@Builder
public record PreSignedImageResponse(
        @Schema(description = "원본 파일 이름", example = "example-image.png")
        String fileName,

        @Schema(description = "S3 저장 주소 (Pre-Signed URL)", example = "https://example.amazonaws.com/bucket/example-image.png?X-Amz-Signature=...")
        String preSignedUrl,

        @Schema(description = "S3 저장 Key", example = "example-image.png")
        String key
        ) {

    /// 정적 팩토리 메서드
    public static PreSignedImageResponse from(String fileName, String preSignedUrl, String key) {
        return PreSignedImageResponse.builder()
                .fileName(fileName)
                .preSignedUrl(preSignedUrl)
                .key(key)
                .build();
    }
}
