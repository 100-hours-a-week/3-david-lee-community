package bootcamp.kakao.community.platform.user.domain.repository;

import bootcamp.kakao.community.platform.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /// 삭제되지않은 유저를 가져오고자 할 때,
    Optional<User> findByEmailAndDeletedFalse(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    /// 삭제되지않은 유저를 가져오고자 할 때,
    Optional<User> findByIdAndDeletedIsFalse(Long id);
}
