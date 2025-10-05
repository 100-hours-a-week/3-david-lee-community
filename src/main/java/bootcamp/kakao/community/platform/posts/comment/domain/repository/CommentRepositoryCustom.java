package bootcamp.kakao.community.platform.posts.comment.domain.repository;

import bootcamp.kakao.community.common.response.paging.SliceRequest;
import bootcamp.kakao.community.platform.posts.comment.domain.repository.dto.CommentWithChildren;
import org.springframework.data.domain.Slice;

public interface CommentRepositoryCustom {

    /**
     * NoOffset을 기반으로 조회한다.
     * @param sliceRequest  슬라이싱 요청
     * @param postId        게시글 ID
     */
    Slice<CommentWithChildren> findCommentsByCursor(SliceRequest sliceRequest, Long postId);

}
