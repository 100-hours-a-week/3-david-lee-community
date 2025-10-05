package bootcamp.kakao.community.platform.posts.comment.application.dto;

import bootcamp.kakao.community.platform.posts.comment.domain.entity.Comment;
import bootcamp.kakao.community.platform.user.application.dto.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

@Schema(
        name = "[응답][댓글] 댓글 응답 Response",
        description = "댓글 정보를 응답하기 위한 DTO입니다."
)
@Builder
public record CommentResponse(
        @Schema(description = "댓글 작성자 정보")
        UserResponse user,

        @Schema(description = "부모 댓글 아이디", example = "null")
        Long parentId,

        @Schema(description = "댓글 본문", example = "정말 좋은 게시글이네요!")
        String content,

        @Schema(description = "댓글 수정 가능 여부", example = "true")
        boolean editable
) {

    /// 정적 팩토리 메서드
    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .user(UserResponse.from(comment.getUser()))
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .content(comment.getContent())
                .build();
    }

    public static Slice<CommentResponse> from(Slice<Comment> posts) {

        /// 값 생성
        List<CommentResponse> commentResponses = posts.stream()
                .map(CommentResponse::from)
                .toList();

        /// Slice 객체 생성
        return new SliceImpl<>(commentResponses, posts.getPageable(), posts.hasNext());
    }

}
