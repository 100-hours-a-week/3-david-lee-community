package bootcamp.kakao.community.platform.posts.comment.presentation;

import bootcamp.kakao.community.common.aop.HttpSessionId;
import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.common.response.paging.SliceRequest;
import bootcamp.kakao.community.common.response.paging.SliceResponse;
import bootcamp.kakao.community.platform.posts.comment.application.CommentUseCase;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentListResponse;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentRequest;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentResponse;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentUpdateRequest;
import bootcamp.kakao.community.platform.posts.comment.presentation.swagger.CommentApiSpec;
import bootcamp.kakao.community.platform.user.domain.entity.UserRole;
import bootcamp.kakao.community.security.auth.annotation.Auth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comments")
public class CommentApi implements CommentApiSpec {

    private final CommentUseCase service;

    /// 댓글 생성
    @Auth(role = UserRole.MEMBER)
    @PostMapping
    public ApiResponse<CommentResponse> createComment(
            @RequestBody @Valid CommentRequest request,
            @HttpSessionId Long userId
    ) {

        /// 서비스 실행
        var response = service.createComment(request, userId);

        /// 리턴
        return ApiResponse.created(response);
    }

    /// 게시글에 따른 댓글 조회
    @Auth(role = UserRole.MEMBER)
    @GetMapping
    public ApiResponse<SliceResponse<CommentListResponse>> listComments(
            SliceRequest sliceRequest,
            @RequestParam Long postId,
            @HttpSessionId Long userId
    ) {

        /// 서비스 실행
        SliceResponse<CommentListResponse> response = service.getComments(sliceRequest, postId, userId);

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 댓글 수정
    @Auth(role = UserRole.MEMBER)
    @PatchMapping("/{commentId}")
    public ApiResponse<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequest request,
            @HttpSessionId Long userId) {

        /// 서비스 실행
        service.updateComment(commentId, request, userId);

        /// 리턴
        return ApiResponse.updated();
    }


    /// 댓글 삭제
    @Auth(role = UserRole.MEMBER)
    @PutMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long commentId,
            @HttpSessionId Long userId
    ){

        /// 서비스 실행
        service.deleteComment(commentId, userId);

        /// 리턴
        return ApiResponse.deleted();
    }



}
