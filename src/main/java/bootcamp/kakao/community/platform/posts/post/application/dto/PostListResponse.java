package bootcamp.kakao.community.platform.posts.post.application.dto;

import bootcamp.kakao.community.common.util.DateUtil;
import bootcamp.kakao.community.platform.posts.post.domain.entity.Post;
import bootcamp.kakao.community.platform.user.application.dto.UserResponse;
import lombok.Builder;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

@Schema(
        name = "[응답][게시글] 게시글 목록 조회 Response",
        description = "게시글 목록 조회를 위한 DTO입니다."
)
@Builder
public record PostListResponse(
        UserResponse user,

        @Schema(description = "게시글 ID", example = "1")
        Long id,

        @Schema(description = "카테고리", example = "자유게시판")
        String category,

        @Schema(description = "썸네일 URL", example = "https://example.com/image.png")
        String thumbnailUrl,

        @Schema(description = "제목", example = "오늘의 게시글")
        String title,

        @Schema(description = "내용", example = "오늘은 날씨가 좋네요.")
        String content,

        @Schema(description = "조회수", example = "102")
        long viewCount,

        @Schema(description = "댓글수", example = "18")
        long commentCount,

        @Schema(description = "좋아요수", example = "18")
        long likeCount,

        @Schema(description = "작성일시", example = "2023-10-02T14:30:00")
        String createdAt

) {
    /// 정적 팩토리 메서드
    public static PostListResponse from(Post post) {

        /// 게시글 정보 조회
        var stat = post.getPostStat();

        return PostListResponse.builder()
                .id(post.getId())
                .user(UserResponse.from(post.getUser()))
                .category(post.getCategory().getName())
                .thumbnailUrl(post.getThumbnailUrl())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(stat.getViewCount())
                .commentCount(stat.getCommentCount())
                .likeCount(stat.getLikeCount())
                .createdAt(post.getCreatedDate().toString())
                .build();
    }

    /// 정적 팩토리 메서드
    public static PostListResponse from(Post post, Long viewCount, Long commentCount, Long likeCount) {

        return PostListResponse.builder()
                .id(post.getId())
                .user(UserResponse.from(post.getUser()))
                .category(post.getCategory().getName())
                .thumbnailUrl(post.getThumbnailUrl())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(viewCount)
                .commentCount(commentCount)
                .likeCount(likeCount)
                .createdAt(DateUtil.formatPostDate(post.getCreatedDate()))
                .build();
    }

    /// 정적 팩토리 메서드
    public static Slice<PostListResponse> from(Slice<Post> posts) {

        /// 게시글 정보 조회
        List<PostListResponse> responses = posts.stream()
                .map(PostListResponse::from)
                .toList();

        /// Slice 리턴
        return new SliceImpl<>(responses, posts.getPageable(), posts.hasNext());
    }
}
