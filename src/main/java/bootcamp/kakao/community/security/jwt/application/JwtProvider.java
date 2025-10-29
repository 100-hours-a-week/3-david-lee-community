package bootcamp.kakao.community.security.jwt.application;

import bootcamp.kakao.community.security.jwt.application.dto.JwtTokenRequest;
import bootcamp.kakao.community.security.jwt.domain.entity.JwtRefreshToken;
import bootcamp.kakao.community.security.jwt.domain.repository.JwtRefreshTokenRepository;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static bootcamp.kakao.community.common.util.KeyUtil.*;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${auth.jwt.access.expiration}")
    private long accessExpiration;

    @Value("${auth.jwt.refresh.expiration}")
    private long refreshExpiration;

    /// 비밀키
    private final SecretKey secretKey;
    /// 레디스 저장소
    private final JwtRefreshTokenRepository repository;


    // =================
    //  퍼블릭 로직
    // =================


    /// 액세스 토큰 발급
    public String createAccessToken(JwtTokenRequest userInfo) {

        /// 액세스 토큰 생성
        return createToken(userInfo, accessExpiration);
    }

    /// 리프레쉬 토큰 발급
    public String createRefreshToken(String deviceType, JwtTokenRequest userInfo) {

        /// 리프레쉬 토큰 생성
        String refreshToken = createToken(userInfo, refreshExpiration);

        /// 객체 생성
        var token = JwtRefreshToken.of(userInfo.userId(), refreshToken, deviceType, refreshExpiration);

        /// 레디스에 저장하기
        return repository.save(token)
                .getRefreshToken();
    }


    // =================
    //  내부 공통 로직
    // =================

    /// 토큰 발급 내부 로직
    private String createToken(JwtTokenRequest tokenInfo, long expiredTime) {

        /// 현재 시간 기준으로 생성
        Instant now = Instant.now();

        /// 토큰 만료일 설정
        Date expiredAt = Date.from(now.plus(Duration.ofSeconds(expiredTime)));

        /// JWT 내용 생성
        Map<String, Object> claims = new HashMap<>();
        claims.put(ID_CLAIM, tokenInfo.userId());               /// userId 넣기
        claims.put(ROLE_CLAIM, tokenInfo.role().getRole());     /// Role 정보 넣기
        claims.put(IP_CLAIM, tokenInfo.ip());                   /// IP 정보 넣기
        claims.put(DEVICE_CLAIM, tokenInfo.deviceType());       /// 디바이스 정보 넣기

        return Jwts.builder()
                .setIssuer("kakao-bootcamp-community") // 작성자
                .setClaims(claims)  // 페이로드
                .setIssuedAt(Date.from(now)) // 발급 시간
                .setExpiration(expiredAt) // 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256)  // 비밀키 서명하기
                .compact();
    }
}
