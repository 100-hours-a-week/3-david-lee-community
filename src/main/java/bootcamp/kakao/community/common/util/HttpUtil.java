package bootcamp.kakao.community.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import static bootcamp.kakao.community.common.util.KeyUtil.*;

@Component
public class HttpUtil {

    @Value("${auth.jwt.refresh.expiration}")
    private long refreshExpiration;

    @Value("${auth.jwt.secure}")
    private boolean secure;

    @Value("${auth.jwt.sameSite}")
    private String sameSite;

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
        createCookie(httpServletResponse, REFRESH_TOKEN, refreshToken, refreshExpiration);
    }

    /// 리프레쉬 토큰을 삭제하기
    public void removeRefreshTokenCookie(HttpServletResponse httpServletResponse) {

        /// 쿠키 생성
        createCookie(httpServletResponse, REFRESH_TOKEN, null, 0);
    }

    /// Header에서 어떤 디바이스인지 체크
    public String getDeviceType(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("User-Agent");
    }

    /// 요청자의 정보를 헤더에서 조회하기 위한 함수
    public HeaderInfo getClientInfo(HttpServletRequest request) {

        /// IP
        String ip = getClientIp(request);

        /// 메서드
        String httpMethod = request.getMethod();

        /// 요청 주소
        String uri = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8);

        /// 요청자
        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "익명";

        return new HeaderInfo(ip, httpMethod, uri, username);
    }

    /// 헤더의 값을 전달하기 위해서 레코드 클래스 생성
    public record HeaderInfo(String ip, String httpMethod, String uri, String userName) {

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
    private void createCookie(HttpServletResponse response, String cookieName, String cookieValue, long maxAge) {

        ResponseCookie cookie = ResponseCookie.from(cookieName, cookieValue)
                .maxAge(maxAge)
                .path("/")
                .httpOnly(true)
                .secure(secure)  // Dev/Prod 환경에 따라 설정됨
                .sameSite(sameSite)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

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

    /// 요청자의 실제 IP를 조회하기 위한 함수
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        /// X-Forwarded-For이 있다면
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 여러 개라면 첫 번째 값이 클라이언트 IP
            return ip.split(",")[0].trim();
        }

        /// X-Forwarded-For이 없다면
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}

