package bootcamp.kakao.community.platform.posts.post_likes.presentation;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.platform.posts.post_likes.application.PostLikeUseCase;
import bootcamp.kakao.community.platform.posts.post_likes.application.dto.PostLikeRequest;
import bootcamp.kakao.community.platform.posts.post_likes.presentation.swagger.PostLikeApiSpec;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import bootcamp.kakao.community.security.auth.annotation.CurrentUserId;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/posts/likes")
@RequiredArgsConstructor
public class PostLikeApi implements PostLikeApiSpec {

    private final PostLikeUseCase service;

    /// 좋아요 생성
    @PostMapping
    public ApiResponse<Void> like(
            @RequestBody @Valid PostLikeRequest request,
            @CurrentUserId Long userId) {

        /// 서비스
        service.like(request, userId);

        /// 리턴
        return ApiResponse.created();
    }

    /// 좋아요 취소
    @DeleteMapping
    public ApiResponse<Void> unlike(
            @RequestParam Long postId,
            @CurrentUserId Long userId) {

        /// 서비스
        service.unlike(postId, userId);

        /// 리턴
        return ApiResponse.deleted();
    }

}
