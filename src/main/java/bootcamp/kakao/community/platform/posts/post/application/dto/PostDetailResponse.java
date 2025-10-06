package bootcamp.kakao.community.platform.posts.post.application.dto;

import bootcamp.kakao.community.platform.images.post_images.application.dto.PostImageResponse;
import bootcamp.kakao.community.platform.images.post_images.domain.entity.PostImage;
import bootcamp.kakao.community.platform.posts.post.domain.entity.Post;
import bootcamp.kakao.community.platform.user.application.dto.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.*;

@Schema(
        name = "[응답][게시글] 게시글 상세 조회 Response",
        description = "게시글 상세 조회를 위한 DTO입니다."
)
@Builder
public record PostDetailResponse(
        UserResponse author,

        @Schema(description = "게시글 ID", example = "1")
        Long id,

        @Schema(description = "카테고리", example = "Q&A")
        String category,

        @Schema(description = "제목", example = "SpringBoot로 게시판 만들기")
        String title,

        @Schema(description = "내용", example = "SpringBoot를 활용한 게시판 개발 방법을 공유합니다.")
        String content,

        @Schema(description = "좋아요 수", example = "53")
        long likeCount,

        @Schema(description = "조회수", example = "1201")
        long viewCount,

        @Schema(description = "댓글수", example = "37")
        long commentCount,

        @Schema(description = "이미지 목록", example = "[{\"thumbnailUrl\": \"https://sample.com/image1.png\"}]")
        List<PostImageResponse> image,

        @Schema(description = "좋아요 여부", example = "true")
        boolean liked,

        @Schema(description = "수정/삭제 가능 여부", example = "false")
        boolean editable,

        @Schema(description = "글 작성 시각", example = "2023-10-02T14:30:00")
        String createdAt,

        @Schema(description = "글 수정 시각", example = "2023-10-03T09:00:00")
        String updatedAt
) {

    /// (비회원용) 정적 팩토리 메서드
    public static PostDetailResponse from(Post post, List<PostImage> imageList, Long viewCount, Long commentCount, Long likeCount) {
        /// 정보 조회
        var stat = post.getPostStat();

        return PostDetailResponse.builder()
                .id(post.getId())
                .author(UserResponse.from(post.getUser()))
                .category(post.getCategory().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .likeCount(likeCount == null ? 0 : likeCount)
                .viewCount(viewCount == null ? 0 : viewCount)
                .commentCount(commentCount == null ? 0 : commentCount)
                .image(PostImageResponse.from(imageList))
                .liked(false)
                .editable(false)
                .createdAt(post.getCreatedDate().toString())
                .updatedAt(post.getModifiedDate().toString())
                .build();
    }

    /// (회원용) 정적 팩토리 메서드
    public static PostDetailResponse from(Post post, List<PostImage> imageList, Long viewCount, Long commentCount, Long likeCount, boolean liked, boolean editable) {
        return PostDetailResponse.builder()
                .author(UserResponse.from(post.getUser()))
                .category(post.getCategory().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .likeCount(likeCount == null ? 0 : likeCount)
                .viewCount(viewCount == null ? 0 : viewCount)
                .commentCount(commentCount == null ? 0 : commentCount)
                .image(PostImageResponse.from(imageList))
                .liked(liked)
                .editable(editable)
                .createdAt(post.getCreatedDate().toString())
                .updatedAt(post.getModifiedDate().toString())
                .build();
    }
}
