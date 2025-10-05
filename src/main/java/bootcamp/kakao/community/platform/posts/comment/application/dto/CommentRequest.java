package bootcamp.kakao.community.platform.posts.comment.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[요청][댓글] 댓글 등록 Request", description = "댓글 등록을 위한 DTO입니다.")
public record CommentRequest(
        @Schema(description = "부모 댓글 아이디", example = "null")
        Long parentId,

        @Schema(description = "게시글 아이디", example = "1")
        Long postId,

        @Schema(description = "댓글 내용", example = "이 글에 공감합니다!")
        String content
) {
}
