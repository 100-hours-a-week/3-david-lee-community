package bootcamp.kakao.community.platform.images.post_images.application.dto;

import bootcamp.kakao.community.common.util.ImageUtil;
import bootcamp.kakao.community.platform.images.post_images.domain.entity.PostImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

@Schema(
        name = "[응답][포스트 이미지] 포스트 이미지 응답 Response",
        description = "포스트 이미지 정보를 응답하는 DTO입니다."
)
@Builder
public record PostImageResponse(

        @Schema(description = "이미지 Key", example = "images/image1.png")
        String imageKey,

        @Schema(description = "이미지 URL", example = "https://example.com/images/image1.png")
        String imageUrl,

        @Schema(description = "이미지 노출 순서", example = "1")
        int order
) {

    /// 정적 팩토리 메서드
    public static PostImageResponse from(PostImage postImage) {

        return PostImageResponse.builder()
                .imageKey(postImage.getImage().getKey())
                .imageUrl(ImageUtil.getUrlByKey(postImage.getImage().getKey()))
                .order(postImage.getOrd())
                .build();
    }


    /// 정적 팩토리 메서드
    public static List<PostImageResponse> from(List<PostImage> postImages) {

        return postImages.stream()
                .map(PostImageResponse::from)
                .toList();
    }

}
