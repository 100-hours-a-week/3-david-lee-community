package bootcamp.kakao.community.platform.posts.category.application.dto;

import bootcamp.kakao.community.platform.posts.category.domain.entity.Category;
import lombok.Builder;
import java.util.List;

/// 카테고리 응답
@Builder
public record CategoryResponse(
        Long id,
        Long parentId,
        String name
) {

    /// 정적 팩토리 메서드
    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .parentId(category.getParent() == null ? null : category.getParent().getId())
                .name(category.getName())
                .build();
    }

    /// 정적 팩토리 메서드
    public static List<CategoryResponse> from(List<Category> categories) {
        return categories.stream()
                .map(CategoryResponse::from)
                .toList();
    }

}
