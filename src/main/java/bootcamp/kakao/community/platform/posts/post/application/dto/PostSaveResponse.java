package bootcamp.kakao.community.platform.posts.post.application.dto;

import bootcamp.kakao.community.platform.posts.post.domain.entity.Post;
import lombok.Builder;

@Builder
public record PostSaveResponse(
        Long postId
) {

    /// 정적 팩토리 메서드
    public static PostSaveResponse from(Post post) {
        return PostSaveResponse.builder()
                .postId(post.getId())
                .build();
    }

}
