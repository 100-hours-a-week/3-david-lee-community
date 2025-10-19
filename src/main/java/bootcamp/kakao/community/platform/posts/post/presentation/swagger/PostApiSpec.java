package bootcamp.kakao.community.platform.posts.post.presentation.swagger;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.common.response.paging.SliceRequest;
import bootcamp.kakao.community.common.response.paging.SliceResponse;
import bootcamp.kakao.community.platform.posts.post.application.dto.*;
import bootcamp.kakao.community.security.auth.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "게시글 API", description = "게시글 관련 API 입니다.")
public interface PostApiSpec {

    /// 글 작성
    @Operation(
            summary = "게시글 작성 API",
            description = "게시글을 작성하는 API 입니다."
    )
    ApiResponse<PostSaveResponse> createPost(
            @RequestBody @Valid PostRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails);


    /// 글 상세 조회
    @Operation(
            summary = "게시글 상세 조회 API",
            description = "게시글을 조회하는 API 입니다."
    )
    ApiResponse<PostDetailResponse> readPost(@PathVariable Long postId,
                                             @AuthenticationPrincipal CustomUserDetails customUserDetails);


    /// 글 목록 조회
    @Operation(
            summary = "게시글 목록 조회 API",
            description = "게시글 목록을 조회하는 API 입니다."
    )
    ApiResponse<SliceResponse<PostListResponse>> readPosts(
            SliceRequest sliceRequest,
            @RequestParam Long categoryId);


    /// 글 수정
    @Operation(
            summary = "게시글 수정 API",
            description = "게시글을 수정하는 API 입니다."
    )
    ApiResponse<Void> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails);


    /// 글 삭제 (소프트 삭제)
    @Operation(
            summary = "게시글 삭제 API",
            description = "게시글을 삭제하는 API 입니다."
    )
    ApiResponse<Void> delete(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails);

}
