package bootcamp.kakao.community.platform.posts.category.domain.repository;

import bootcamp.kakao.community.platform.posts.category.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.parent.id IS NULL")
    List<Category> findByParentNull();

}
