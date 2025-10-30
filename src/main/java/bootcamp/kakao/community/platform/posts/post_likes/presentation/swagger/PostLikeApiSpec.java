package bootcamp.kakao.community.platform.posts.post_likes.presentation.swagger;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.platform.posts.post_likes.application.dto.PostLikeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import bootcamp.kakao.community.security.auth.annotation.CurrentUserId;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "게시글 좋아요 API", description = "게시글 좋아요 생성/취소하는 API 입니다.")
public interface PostLikeApiSpec {

    /// 좋아요
    @Operation(
            summary = "좋아요 생성 API",
            description = "좋아요를 생성합니다."
    )
    ApiResponse<Void> like(
            @RequestBody @Valid PostLikeRequest request,
            @CurrentUserId Long userId);

    /// 좋아요 취소
    @Operation(
            summary = "좋아요 취소 API",
            description = "좋아요를 취소합니다."
    )
    ApiResponse<Void> unlike(
            @Parameter(example = "1")
            @RequestParam Long postId,
            @CurrentUserId Long userId);


}
