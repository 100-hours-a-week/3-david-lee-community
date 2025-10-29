package bootcamp.kakao.community.platform.posts.comment.presentation.swagger;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.common.response.paging.SliceRequest;
import bootcamp.kakao.community.common.response.paging.SliceResponse;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentListResponse;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentRequest;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentResponse;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import bootcamp.kakao.community.security.auth.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "댓글 API", description = "댓글 관련 API 입니다.")
public interface CommentApiSpec {

    /// 댓글 작성
    @Operation(
            summary = "댓글 작성 API",
            description = "댓글을 작성하는 API 입니다."
    )
    ApiResponse<CommentResponse> createComment(
            @RequestBody @Valid CommentRequest request,
            @AuthenticationPrincipal Long userId);

    /// 댓글 조회
    @Operation(
            summary = "댓글 조회 API",
            description = "게시글에 따른 댓글을 조회하는 API 입니다."
    )
    ApiResponse<SliceResponse<CommentListResponse>> listComments(
            SliceRequest sliceRequest,
            @RequestParam Long postId,
            @AuthenticationPrincipal Long userId
    );


    /// 댓글 수정
    @Operation(
            summary = "댓글 수정 API",
            description = "게시글에 따른 댓글을 수정하는 API 입니다."
    )
    ApiResponse<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequest request,
            @AuthenticationPrincipal Long userId);


    /// 댓글 삭제
    @Operation(
            summary = "댓글 삭제 API",
            description = "게시글에 따른 댓글을 삭제하는 API 입니다."
    )
    ApiResponse<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Long userId);
}
