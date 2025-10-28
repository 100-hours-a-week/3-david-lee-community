package bootcamp.kakao.community.security.session.application;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.SecurityErrorCode;
import bootcamp.kakao.community.common.response.code.UserErrorCode;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class SessionProvider {

    /// 세션 저장소는 레디스에 저장
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${auth.session.expiration}")
    private long sessionExpiration;


    /// 세션을 쿠키에 담아서 사용한다.
    public String createSessionId(User user){

        /// 세션 아이디를 바탕으로 넣기
        String sessionId = UUID.randomUUID().toString();

        /// 저장할 객체로
        var sessionData = UserRedisSessionData.from(user);

        /// 직렬화
        redisTemplate.opsForValue().set(sessionId, sessionData, Duration.ofSeconds(sessionExpiration));

        /// 리턴
        return sessionId;
    }

    /// 세션 ID를 바탕으로 유저 정보 얻기
    public Long getUserBySession(Optional<String> sessionId){

        if(sessionId.isEmpty()){
            throw new CustomException(SecurityErrorCode.BAD_REQUEST_SESSION);
        }

        /// 세션 저장소에서 유저 가져오기
        UserRedisSessionData sessionData = (UserRedisSessionData) redisTemplate.opsForValue().get(sessionId.get());

        /// 없다면 예외 던지기
        if (sessionData == null) {
            throw new CustomException(UserErrorCode.NOT_FOUND_USER);
        }

        /// 리턴
        return sessionData.userId;
    }

    /// 세션 ID를 바탕으로 유저 정보 없애기
    public void removeUserBySession(String sessionId){

        /// 세션 저장소에서 유저 가져오기
        UserRedisSessionData sessionData = (UserRedisSessionData) redisTemplate.opsForValue().get(sessionId);

        /// 없다면 예외 던지기
        if (sessionData == null) {
            throw new CustomException(UserErrorCode.NOT_FOUND_USER);
        }

        redisTemplate.delete(sessionId);

    }

    /// 레디스에 저장할 객체 구조화
    protected record UserRedisSessionData(
            Long userId,
            String Role
    ){
        public static UserRedisSessionData from(User user){
            return new UserRedisSessionData(user.getId(), user.getRole().getRole());
        }

    }

}
