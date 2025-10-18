package bootcamp.kakao.community.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import static bootcamp.kakao.community.common.util.KeyUtil.*;

@Component
public class HttpUtil {

    @Value("${auth.jwt.access.expiration}")
    private long accessExpiration;

    @Value("${auth.jwt.refresh.expiration}")
    private long refreshExpiration;

    /// 액세스 토큰 헤더 가져오기
    public Optional<String> getAccessToken(HttpServletRequest request) {

        /// 헤더에서 가져오기
        Optional<String> accessTokenOptional = extractHeader(request, AUTHORIZATION);

        if (accessTokenOptional.isPresent()) {
            /// 있다면 Bearer 빼고 가져오기
            return Optional.of(getBearerToken(accessTokenOptional.get()));
        }
        return Optional.empty();

    }

    /// 리프레쉬 토큰 쿠키 가져오기
    public Optional<String> getRefreshToken(HttpServletRequest request) {
        return extractCookie(request, REFRESH_TOKEN);
    }

    /// 액세스 토큰을 쿠키에 저장하기
    public void addAccessTokenHeader(HttpServletResponse httpServletResponse, String accessToken) {

        /// Bearer 추가하기
        String bearerAccessToken = setBearerToken(accessToken);

        /// 헤더 저장
        createHeader(httpServletResponse, AUTHORIZATION, bearerAccessToken);
    }

    /// 리프레쉬 토큰을 쿠키에 저장하기
    public void addRefreshTokenCookie(HttpServletResponse httpServletResponse, String refreshToken) {

        /// 쿠키 생성
        Cookie cookie = createCookie(REFRESH_TOKEN, refreshToken, refreshExpiration);

        /// 쿠키 저장
        httpServletResponse.addCookie(cookie);
    }

    /// 리프레쉬 토큰을 삭제하기
    public void removeRefreshTokenCookie(HttpServletResponse httpServletResponse) {
        Cookie cookie = createCookie(REFRESH_TOKEN, null, 0);
        httpServletResponse.addCookie(cookie);
    }

    /// Header에서 어떤 디바이스인지 체크
    public String getDeviceType(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("User-Agent");
    }

    // =================
    //  내부 공통 함수
    // =================

    /// 헤더로 발급하기
    private void createHeader(HttpServletResponse httpServletResponse, String headerName, String headerValue) {

        /// 헤더 설정하기
        httpServletResponse.setHeader(headerName, headerValue);
    }

    /// 헤더에서 값 가져오기
    private Optional<String> extractHeader(HttpServletRequest httpServletRequest, String headerName) {

        /// 헤더 가져오기
        return Optional.ofNullable(httpServletRequest.getHeader(headerName));
    }


    /// 쿠키 생성하기
    private Cookie createCookie(String cookieName, String accessToken, long accessExpiration) {

        Cookie cookie = new Cookie(cookieName, accessToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) accessExpiration);
        return cookie;
    }

    /// 쿠키에서 토큰 가져오기
    private Optional<String> extractCookie(HttpServletRequest httpServletRequest, String type) {

        /// 쿠키 가져오기
        Cookie[] cookies = httpServletRequest.getCookies();

        /// 쿠키가 존재한다면,
        if (cookies != null) {
            for (Cookie cookie : cookies) {

                /// 해당 타입의 쿠키만 추출
                if (cookie.getName().equals(type)) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    /// 토큰에서 Bearer 추가하기
    private String setBearerToken(String accessToken) {

        /// 토큰에서 Bearer 추가하기
        return BEARER + " " + accessToken;
    }

    /// 헤더에서 Bearer 빼고 가져오기
    private String getBearerToken(String headerToken) {

        /// Bearer (띄어쓰기 포함 7글자) 빼고 가져오기
        return headerToken.substring(7);

    }
}
