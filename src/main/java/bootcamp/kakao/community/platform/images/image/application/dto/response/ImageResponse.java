package bootcamp.kakao.community.platform.images.image.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(
        name = "[응답][이미지] 이미지 응답 Response",
        description = "S3에 저장된 이미지 정보를 응답하기 위한 DTO입니다."
)
@Builder
public record ImageResponse(

        @Schema(description = "S3 저장 주소 (URL)", example = "https://example.s3.ap-northeast-2.amazonaws.com/6cab0f56-4644-4ebf-b076-0eb76fe2a7ec.jpeg")
        String imageUrl
) {
        /// 정적 팩토리 메서드
        public static ImageResponse from(String imageUrl) {
            return ImageResponse.builder()
                    .imageUrl(imageUrl)
                    .build();
        }

}
