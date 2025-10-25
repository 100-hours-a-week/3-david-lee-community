package bootcamp.kakao.community.platform.posts.comment.presentation;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.common.response.paging.SliceRequest;
import bootcamp.kakao.community.common.response.paging.SliceResponse;
import bootcamp.kakao.community.platform.posts.comment.application.CommentUseCase;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentListResponse;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentRequest;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentResponse;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentUpdateRequest;
import bootcamp.kakao.community.platform.posts.comment.presentation.swagger.CommentApiSpec;
import bootcamp.kakao.community.security.auth.domain.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comments")
public class CommentApi implements CommentApiSpec {

    private final CommentUseCase service;

    /// 댓글 생성
    @PostMapping
    public ApiResponse<CommentResponse> createComment(
            @RequestBody @Valid CommentRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        /// 서비스 실행
        var response = service.createComment(request, customUserDetails.getId());

        /// 리턴
        return ApiResponse.created(response);
    }

    /// 게시글에 따른 댓글 조회
    @GetMapping
    public ApiResponse<SliceResponse<CommentListResponse>> listComments(
            SliceRequest sliceRequest,
            @RequestParam Long postId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        /// 유저가 없다면 null 저장
        Long userId = customUserDetails != null ? customUserDetails.getId() : null;

        /// 서비스 실행
        SliceResponse<CommentListResponse> response = service.getComments(sliceRequest, postId, userId);

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 댓글 수정
    @PatchMapping("/{commentId}")
    public ApiResponse<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        /// 서비스 실행
        service.updateComment(commentId, request, customUserDetails.getId());

        /// 리턴
        return ApiResponse.updated();
    }


    /// 댓글 삭제
    @PutMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){

        /// 서비스 실행
        service.deleteComment(commentId, customUserDetails.getId());

        /// 리턴
        return ApiResponse.deleted();
    }



}
