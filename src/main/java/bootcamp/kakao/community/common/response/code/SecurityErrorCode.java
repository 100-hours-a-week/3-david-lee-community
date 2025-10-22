package bootcamp.kakao.community.common.response.code;

import bootcamp.kakao.community.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 보안 예외처리 클래스입니다.
 */
@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ErrorCode {

    // ========================
    // 400 Bad Request
    // ========================

    // ========================
    // 401 Unauthorized
    // ========================
    ACCESS_TOKEN_EXPIRED(401_000, HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다."),
    ACCESS_TOKEN_INVALID(401_001, HttpStatus.UNAUTHORIZED, "액세스 토큰이 유효하지 않습니다."),
    ACCESS_TOKEN_UNSUPPORTED(401_002, HttpStatus.UNAUTHORIZED, "액세스 토큰이 지원하지 않는 형식입니다."),
    ACCESS_TOKEN_MALFORMED(401_004, HttpStatus.UNAUTHORIZED, "액세스 토큰의 구조가 깨진 형식입니다."),
    ACCESS_TOKEN_NOT_FOUND(401_002, HttpStatus.UNAUTHORIZED, "액세스 토큰이 존재하지 않습니다."),

    REFRESH_TOKEN_EXPIRED(401_004, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 만료되었습니다."),
    REFRESH_TOKEN_INVALID(401_005, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_UNSUPPORTED(401_006, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 지원하지 않는 토큰 형식입니다."),
    REFRESH_INVALID_LOGIN(401_007, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 없기에 재로그인이 필요합니다."),
    TOKEN_NOT_FOUND_COOKIE(401_010, HttpStatus.UNAUTHORIZED, "쿠키에 리프레시 토큰이 존재하지 않습니다."),


    // ========================
    // 403 Forbidden
    // ========================

    FORBIDDEN(403_000, HttpStatus.FORBIDDEN, "접속 권한이 없습니다."),
    ACCESS_DENY(403_001, HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
    UNAUTHORIZED_POST_ACCESS(403_002, HttpStatus.FORBIDDEN, "해당 게시글에 접근할 권한이 없습니다."),

    // ========================
    // 404 Not Found
    // ========================
    USER_NOT_FOUND_IN_COOKIE(404_002, HttpStatus.NOT_FOUND, "쿠키에서 사용자 정보를 찾을 수 없습니다.");

    // ========================
    // 500 Internal Server Error
    // ========================


    /**
     * 에러 코드 (고유값)
     */
    private final Integer code;

    /**
     * HTTP 상태 코드
     */
    private final HttpStatus httpStatus;

    /**
     * 에러 메시지
     */
    private final String message;
}
