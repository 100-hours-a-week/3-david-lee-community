package bootcamp.kakao.community.platform.posts.post.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(name = "[요청][게시글] 게시글 수정 Request", description = "게시글 수정을 위한 요청 DTO입니다.")
public record PostUpdateRequest(
        @Schema(description = "카테고리 ID", example = "1001")
        Long categoryId,

        @Schema(description = "제목", example = "수정된 게시글 제목")
        String title,

        @Schema(description = "내용", example = "수정된 게시글 내용")
        String content,

        @Schema(description = "첨부 이미지 URL 리스트", example = "[\"image1.png\", \"image2.png\"]")
        @Size(max = 10,message = "이미지는 최대 10장까지 가능합니다.")
        List<String> imageKeys
) {
}
