package bootcamp.kakao.community.platform.posts.comment.domain.repository.dto;

import bootcamp.kakao.community.platform.posts.comment.domain.entity.Comment;
import lombok.Builder;
import java.util.List;

/// 댓글과 해당하는 대댓글 조회하기
@Builder
public record CommentWithChildren(
        Comment root,
        List<Comment> children
) {

    /// 정적 팩토리 메서드
    public static CommentWithChildren from(Comment root, List<Comment> children) {
        return CommentWithChildren.builder()
                .root(root)
                .children(children)
                .build();
    }
    

}
