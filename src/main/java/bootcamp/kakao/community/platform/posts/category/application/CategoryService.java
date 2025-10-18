package bootcamp.kakao.community.platform.posts.category.application;

import bootcamp.kakao.community.platform.posts.category.application.dto.CategoryRequest;
import bootcamp.kakao.community.platform.posts.category.application.dto.CategoryResponse;
import bootcamp.kakao.community.platform.posts.category.domain.entity.Category;
import bootcamp.kakao.community.platform.posts.category.domain.repository.CategoryRepository;
import bootcamp.kakao.community.platform.user.application.UserUseCase;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import bootcamp.kakao.community.platform.user.domain.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryUseCase{

    private final CategoryRepository repository;

    /// 외부 의존성
    private final UserUseCase userService;

    /// 카테고리 추가하기
    @Override
    @Transactional
    public void createCategory(CategoryRequest req, Long userId) {

        Category parent = null;

        /// 운영자만 생성 가능
        /// 운영자라면 삭제하기
        User user = userService.loadUser(userId);

        /// 운영자가 일때만 가능
        if (!(user.getRole() == UserRole.ADMIN)) {
            throw new IllegalStateException("운영자만 카테고리를 생성할 수 있습니다.");
        }

        /// 부모 값이 존재한다면,
        if (req.parentId() != null) {
            parent = repository.findById(req.parentId())
                    .orElseThrow(NoSuchElementException::new);
        }
        Category reqCategory = Category.of(parent, req.name());

        /// 저장
        repository.save(reqCategory);
    }

    /// 카테고리 삭제하기
    @Override
    @Transactional
    public void deleteCategory(Long id, Long userId) {

        /// 카테고리 조회
        Category category = loadCategory(id);

        /// 운영자라면 삭제하기
        User user = userService.loadUser(userId);

        /// 운영자가 일때만 가능
        if (!(user.getRole() == UserRole.ADMIN)) {
            throw new IllegalStateException("운영자만 카테고리를 삭제할 수 있습니다.");
        }

        /// 바로 삭제
        repository.delete(category);
    }

    /// 루트 카테고리 목록 조회하기
    @Override
    public List<CategoryResponse> readRootCategories() {

        /// 카테고리 목록 조회
        List<Category> categories = repository.findByParentNull();

        /// 리턴
        return CategoryResponse.from(categories);
    }

    // =================
    //  외부 사용 로직
    // =================
    @Override
    public Category loadCategory(Long id) {
        return repository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }
}
