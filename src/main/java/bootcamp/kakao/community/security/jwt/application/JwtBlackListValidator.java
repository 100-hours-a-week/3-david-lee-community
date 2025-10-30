package bootcamp.kakao.community.security.jwt.application;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.SecurityErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static bootcamp.kakao.community.common.util.KeyUtil.getBlackList;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtBlackListValidator {

    /// 액세스 토큰 만료
    @Value("${auth.jwt.access.expiration}")
    private long accessExpiration;

    /// 레디스
    private final RedisTemplate<String, Object> redisTemplate;


    /// 블랙 리스트 등록하기
    public void addBlackList(String token) {

        /// 키 가져오기
        String blackList = getBlackList(token);

        /// 레디스에서 저장하기
        redisTemplate.opsForValue().set(blackList, "true", accessExpiration, TimeUnit.SECONDS);
    }


    /// 블랙 리스트 여부 파악하기
    public void checkBlackList(String token) {

        /// 키 가져오기
        String blackList = getBlackList(token);

        /// 레디스에서 존재하는지 체크
        Boolean hasKey = redisTemplate.hasKey(blackList);

        /// 있다면 예외 발생
        if (hasKey) {

            /// 블랙 리스트 403 에러 출력
            throw new CustomException(SecurityErrorCode.FORBIDDEN_BLACKLIST);
        }

    }
}
