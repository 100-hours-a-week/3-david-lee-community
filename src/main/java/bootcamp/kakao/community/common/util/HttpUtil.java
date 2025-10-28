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

    @Value("${auth.session.expiration}")
    private long sessionExpiration;

    /// 세션 ID 가져오기
    public Optional<String> getSessionId(HttpServletRequest request) {

        /// 쿠키에서 세션 ID 가져오기
        return extractCookie(request, SESSION);
    }

    /// 세션 ID 쿠키에 저장하기
    public void addSessionId(HttpServletResponse response, String sessionId) {
        createCookie(response, SESSION, sessionId, sessionExpiration);
    }

    /// 세션 ID 쿠키에서 삭제하기
    public void removeSessionId(HttpServletResponse response) {
        createCookie(response, SESSION, null, 0);
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

    /// 쿠키 생성하기
    private void createCookie(HttpServletResponse response, String cookieName, String cookieValue, long maxAge) {

        ResponseCookie cookie = ResponseCookie.from(cookieName, cookieValue)
                .maxAge(maxAge)
                .path("/")
                .httpOnly(true)     // JS에서 꺼내지 못하게끔
                .secure(false)      // 개발환경이기에 false
                .sameSite("Lax")
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

