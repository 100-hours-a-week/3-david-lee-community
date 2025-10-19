package bootcamp.kakao.community.platform.posts.post.presentation;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.common.response.paging.SliceRequest;
import bootcamp.kakao.community.common.response.paging.SliceResponse;
import bootcamp.kakao.community.platform.posts.post.application.PostCommandUseCase;
import bootcamp.kakao.community.platform.posts.post.application.PostQueryUseCase;
import bootcamp.kakao.community.platform.posts.post.application.dto.*;
import bootcamp.kakao.community.platform.posts.post.presentation.swagger.PostApiSpec;
import bootcamp.kakao.community.security.auth.domain.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostApi implements PostApiSpec {

    private final PostCommandUseCase commandService;
    private final PostQueryUseCase queryService;

    /// 글 작성
    @PostMapping
    public ApiResponse<PostSaveResponse> createPost(
            @RequestBody @Valid PostRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        /// 서비스 작성
        var response = commandService.create(request, customUserDetails.getId());

        /// 리턴
        return ApiResponse.created(response);
    }

    /// 글 상세 조회
    @GetMapping("/{postId}")
    public ApiResponse<PostDetailResponse> readPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {


        /// 유저가 없다면 null 저장
        Long userId = customUserDetails != null ? customUserDetails.getId() : null;

        /// 서비스 읽기
        PostDetailResponse response = queryService.getPost(postId, userId);

        /// 리턴
        return ApiResponse.ok(response);
    }


    /// 글 목록 조회
    @GetMapping()
    public ApiResponse<SliceResponse<PostListResponse>> readPosts(
            SliceRequest sliceRequest,
            @RequestParam Long categoryId) {

        /// 서비스 읽기
        SliceResponse<PostListResponse> response = queryService.getPosts(sliceRequest, categoryId);

        /// 리턴
        return ApiResponse.ok(response);
    }


    /// 글 수정
    @PatchMapping("/{postId}")
    public ApiResponse<Void> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        /// 서비스 수정
        commandService.update(postId, request, customUserDetails.getId());

        /// 리턴
        return ApiResponse.updated();
    }

    /// 글 삭제 (소프트 삭제)
    @PutMapping("/{postId}")
    public ApiResponse<Void> delete(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        /// 서비스 삭제
        commandService.delete(postId, customUserDetails.getId());

        /// 리턴
        return ApiResponse.deleted();
    }
}
