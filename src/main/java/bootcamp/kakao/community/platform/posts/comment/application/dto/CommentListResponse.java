package bootcamp.kakao.community.platform.posts.comment.application.dto;

import bootcamp.kakao.community.platform.posts.comment.domain.entity.Comment;
import bootcamp.kakao.community.platform.posts.comment.domain.repository.dto.CommentWithChildren;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

@Builder
@Schema(name = "[응답][댓글] 댓글 목록 Response", description = "댓글 목록 조회를 위한 DTO입니다.")
public record CommentListResponse(
        @Schema(description = "루트 댓글")
        CommentResponse root,

        @Schema(description = "자식 댓글 리스트")
        List<CommentResponse> children
) {

    /// 정적 팩토리 메서드
    public static CommentListResponse from(CommentWithChildren withChildren, Long userId) {

        Comment root = withChildren.root();
        List<Comment> children = withChildren.children();

        /// 루트
        CommentResponse rootResponse = CommentResponse.from(root, userId);

        /// 자식
        List<CommentResponse> childResponse = children.stream()
                .map(c -> CommentResponse.from(c, userId))
                .toList();

        return CommentListResponse.builder()
                .root(rootResponse)
                .children(childResponse)
                .build();
    }

    /// 정적 팩토리 메서드
    public static Slice<CommentListResponse> from(Slice<CommentWithChildren> withChildren, Long userId) {

        /// 리스트
        List<CommentListResponse> responses = withChildren.stream()
                .map(c -> CommentListResponse.from(c, userId))
                .toList();

        /// 리턴
        return new SliceImpl<>(responses, withChildren.getPageable(), withChildren.hasNext());

    }

}
