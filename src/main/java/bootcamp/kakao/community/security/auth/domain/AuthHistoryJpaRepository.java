package bootcamp.kakao.community.security.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthHistoryJpaRepository extends JpaRepository<AuthHistory, Long> {


    List<AuthHistory> findByUser_Id(Long userId);
}
