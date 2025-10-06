package bootcamp.kakao.community.platform.posts.category.application;

import bootcamp.kakao.community.platform.posts.category.application.dto.CategoryRequest;
import bootcamp.kakao.community.platform.posts.category.application.dto.CategoryResponse;
import bootcamp.kakao.community.platform.posts.category.domain.entity.Category;
import java.util.List;

public interface CategoryUseCase{

    /// 카테고리 추가하기 (운영자만 가능)
    void createCategory(CategoryRequest req, Long userId);

    /// 카테고리 삭제하기 (운영자만 가능)
    void deleteCategory(Long id, Long userId);

    /// 게시글 목록 조회
    List<CategoryResponse> readRootCategories();

    /// 외부 로직
    Category loadCategory(Long id);
}
