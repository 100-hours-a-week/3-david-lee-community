package bootcamp.kakao.community.security.jwt.application;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.CommonErrorCode;
import bootcamp.kakao.community.common.response.code.SecurityErrorCode;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import bootcamp.kakao.community.platform.user.domain.repository.UserRepository;
import bootcamp.kakao.community.security.jwt.domain.entity.JwtRefreshToken;
import bootcamp.kakao.community.security.jwt.domain.repository.JwtRefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.util.NoSuchElementException;

import static bootcamp.kakao.community.common.util.KeyUtil.ID_CLAIM;

@Component
@RequiredArgsConstructor
public class JwtValidator {

    private final SecretKey secretKey;
    private final UserRepository userRepository;
    /// 레디스 저장소
    private final JwtRefreshTokenRepository repository;

    // =================
    //  퍼블릭 로직
    // =================

    /// 액세스 토큰 검증
    public User validateAccessToken(String accessToken) {

        try {
            /// 토큰 자체의 검증성 파악
            assertJwtValid(accessToken);

            /// 검증 완료되었다면 유저 정보 가져오기
            Long userId = getUserIdFromAccessToken(accessToken);

            /// 유저 응답
            User user = userRepository.findById(userId)
                    .orElseThrow(NoSuchElementException::new);

            /// 인증 객체 생성할 유저 가져오기
            return user;

        } catch (ExpiredJwtException e) {
            /// 만료된 토큰
            throw new CustomException(SecurityErrorCode.ACCESS_TOKEN_EXPIRED);

        } catch (SignatureException e) {
            /// 잘못된 서명
            throw new CustomException(SecurityErrorCode.ACCESS_TOKEN_INVALID);

        } catch (MalformedJwtException e) {
            //// 구조가 깨진 토큰
            throw new CustomException(SecurityErrorCode.ACCESS_TOKEN_MALFORMED);

        } catch (UnsupportedJwtException e) {
            /// 지원되지 않는 JWT 형식 (예: 압축/암호화된 JWT)
            throw new CustomException(SecurityErrorCode.ACCESS_TOKEN_UNSUPPORTED);

        } catch (IllegalArgumentException e) {
            /// 토큰이 비어있거나 null
            throw new CustomException(SecurityErrorCode.ACCESS_TOKEN_NOT_FOUND);

        } catch (JwtException e) {
            /// JWT 관련 기타 예외 (상위 클래스)
            throw new CustomException(SecurityErrorCode.ACCESS_TOKEN_INVALID);

        } catch (CustomException e) {
            /// 커스텀 JWT 예외 (예: USER_NOT_FOUND 등)
            throw e;

        } catch (Exception e) {
            /// 예상치 못한 모든 예외
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /// 리프레쉬 토큰 검증
    public JwtRefreshToken validateRefreshToken(String deviceType, String refreshToken) {

        try {
            /// 토큰 자체의 유효성 검증 (서명, 만료일)
            assertJwtValid(refreshToken);

            /// 토큰에서 유저 ID 추출
            Long userId = getUserIdFromAccessToken(refreshToken);

            /// 리턴
            return repository.findByRefreshTokenAndDeviceTypeAndUserId(refreshToken, deviceType, userId)
                    .orElseThrow(() -> new CustomException(SecurityErrorCode.REFRESH_INVALID_LOGIN));


        } catch (ExpiredJwtException e) {
            // 토큰이 '만료'된 경우의 처리
            throw new CustomException(SecurityErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (SignatureException e) {
            // 서명이 잘못된 경우의 처리
            throw new CustomException(SecurityErrorCode.REFRESH_TOKEN_INVALID);
        } catch (MalformedJwtException e) {
            // 토큰 구조가 잘못된 경우의 처리
            throw new CustomException(SecurityErrorCode.REFRESH_TOKEN_UNSUPPORTED);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /// 로그아웃을 위해 사용하는 로직
    public void removeRefreshToken(Long userId, String deviceType, String refreshToken) {

        /// 토큰 추출
        JwtRefreshToken token = validateRefreshToken(deviceType, refreshToken);

        /// 토큰에서 유저 ID 추출
        Long userIdFromAccessToken = token.getUserId();

        /// 유저가 다르다면, 예외 발생
        if (!userIdFromAccessToken.equals(userId)) {
            throw new CustomException(SecurityErrorCode.REFRESH_TOKEN_INVALID);
        }

        /// 레디스에서 토큰 삭제
        repository.delete(token);

    }

    // =================
    //  내부 공통 로직
    // =================

    /// 비밀키로 해석 가능한지 검증
    private void assertJwtValid(String token) {
         Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
    }


    /// 토큰에서 유저ID 추출하기
    private Long getUserIdFromAccessToken(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .get(ID_CLAIM, Long.class);
    }

}
