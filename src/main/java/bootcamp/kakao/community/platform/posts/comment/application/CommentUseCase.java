package bootcamp.kakao.community.platform.posts.comment.application;

import bootcamp.kakao.community.common.response.paging.SliceRequest;
import bootcamp.kakao.community.common.response.paging.SliceResponse;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentListResponse;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentRequest;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentUpdateRequest;
import bootcamp.kakao.community.platform.posts.comment.domain.entity.Comment;

public interface CommentUseCase {

    /// 댓글 작성
    void createComment(CommentRequest request, Long userId);

    /// 게시글에 따른 댓글 목록 조회 (무한스크롤)
    SliceResponse<CommentListResponse> getComments(SliceRequest request, Long postId, Long userId);

    /// 인기 댓글 목록 조회
    SliceResponse<CommentListResponse> getFavoriteComments(SliceRequest request, Long postId);

    /// 댓글 수정
    void updateComment(CommentUpdateRequest request, Long userId);

    /// 댓글 삭제
    void deleteComment(Long commentId, Long userId);

    /// 외부 사용
    Comment loadComment(Long commentId);
}

