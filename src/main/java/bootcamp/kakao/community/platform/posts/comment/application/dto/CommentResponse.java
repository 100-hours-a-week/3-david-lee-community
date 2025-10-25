package bootcamp.kakao.community.platform.posts.comment.application.dto;

import bootcamp.kakao.community.common.util.DateUtil;
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

        @Schema(description = "댓글 아이디", example = "1")
        long id,

        @Schema(description = "부모 댓글 아이디", example = "null")
        Long parentId,

        @Schema(description = "댓글 본문", example = "정말 좋은 게시글이네요!")
        String content,

        @Schema(description = "작성 일자", example = "")
        String createdAt,

        @Schema(description = "게시글 작성자 여부", example = "false")
        boolean writer,

        @Schema(description = "댓글 수정 가능 여부", example = "true")
        boolean editable

) {

    /// 정적 팩토리 메서드
    public static CommentResponse from(Comment comment, Long userId) {

        /// 삭제 여부 체크
        String content;
        if (comment.isDeleted()) {
            content = "삭제된 메시지입니다.";
        } else {
            content = comment.getContent();
        }

        /// 수정 가능 여부, 기본 값 설정
        boolean editable = false;
        boolean writer = false;

        /// 로그인된 상태 바탕으로 댓글 작성자와 유저가 같은지 여부 체크
        if (userId != null && userId.equals(comment.getUser().getId())) {
            editable = true;
        }

        /// 게시글 작성자와 유저가 같은지 여부 체크
        if (comment.getPost().getUser().getId().equals(comment.getUser().getId())) {
            writer = true;
        }

        return CommentResponse.builder()
                .user(UserResponse.from(comment.getUser()))
                .id(comment.getId())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .content(content)
                .createdAt(DateUtil.formatPostDate(comment.getCreatedDate()))
                .editable(editable)
                .writer(writer)
                .build();
    }

    /// 정적 팩토리 메서드 반복
    public static Slice<CommentResponse> from(Slice<Comment> posts, Long userId) {

        /// 값 생성
        List<CommentResponse> commentResponses = posts.stream()
                .map(comment -> CommentResponse.from(comment, userId))
                .toList();

        /// Slice 객체 생성
        return new SliceImpl<>(commentResponses, posts.getPageable(), posts.hasNext());
    }

}
