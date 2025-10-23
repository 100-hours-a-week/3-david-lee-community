package bootcamp.kakao.community.platform.images.image.domain.repository;

import bootcamp.kakao.community.platform.images.image.domain.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
public interface ImageRepository extends JpaRepository<Image, Long> {

    /// URL 바탕으로 조회
    Optional<Image> findByKey(String key);

    /// 여러개 URL 바탕으로 조회
    List<Image> findAllByKeyIn(List<String> urls);

    /// 사용하지 않는 이미지 모음 조회
    List<Image> findByConfirmedFalse();

}
