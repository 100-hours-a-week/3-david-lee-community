package bootcamp.kakao.community.security.auth.application;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.SecurityErrorCode;
import bootcamp.kakao.community.common.response.code.UserErrorCode;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import bootcamp.kakao.community.platform.user.domain.repository.UserRepository;
import bootcamp.kakao.community.security.auth.application.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class SessionService implements SessionUseCase{

    /// 패스워드 암호화
    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    /// 세션 저장소는 레디스에 저장
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${auth.session.expiration}")
    private long sessionExpiration;

    /// 세션을 통한 로그인
    @Override
    @Transactional
    public String login(LoginRequest request) {

        /// DB 검증
        User user = repository.findByEmailAndDeletedFalse(request.email())
                .orElseThrow(() -> new CustomException(SecurityErrorCode.NOT_FOUND_EMAIL));

        /// 패스워드 비교
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(SecurityErrorCode.BAD_REQUEST_LOGIN);
        }

        /// 세션 아이디를 바탕으로 넣기
        String sessionId = UUID.randomUUID().toString();

        /// 객체로 넣기
        redisTemplate.opsForValue().set(sessionId, user, Duration.ofSeconds(sessionExpiration));

        return sessionId;
    }

    /// 세션을 통한 로그아웃
    @Override
    @Transactional
    public void logout(Optional<String> sessionId) {

        /// 세션 키가 없다면 예외 발생
        if(sessionId.isEmpty()){
            throw new CustomException(SecurityErrorCode.BAD_REQUEST_SESSION);
        }

        /// 세션 저장소에서 유저 가져오기
        User o = (User) redisTemplate.opsForValue().get(sessionId);

        /// 없다면 예외 던지기
        if (o == null) {
            throw new CustomException(UserErrorCode.NOT_FOUND_USER);
        }

        /// 레디스에서 삭제
        redisTemplate.delete(sessionId.get());
    }
}
