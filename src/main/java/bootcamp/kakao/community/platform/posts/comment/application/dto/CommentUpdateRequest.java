package bootcamp.kakao.community.platform.posts.comment.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[요청][댓글] 댓글 수정 Request", description = "댓글 수정을 위한 DTO입니다.")
public record CommentUpdateRequest(
        @Schema(description = "수정할 댓글 내용", example = "수정된 댓글 내용입니다.")
        String content
) {
}
