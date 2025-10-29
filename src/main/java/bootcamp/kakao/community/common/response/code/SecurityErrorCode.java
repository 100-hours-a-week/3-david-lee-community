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
    BAD_REQUEST_LOGIN(400_000, HttpStatus.BAD_REQUEST, "로그인할 수 없습니다."),
    BAD_REQUEST_SESSION(400_001, HttpStatus.BAD_REQUEST, "전달할 세션 키가 없습니다."),


    // ========================
    // 401 Unauthorized
    // ========================
    SESSION_EXPIRED(401_000, HttpStatus.UNAUTHORIZED, "세션이 만료되었습니다."),
    SESSION_INVALID(401_001, HttpStatus.UNAUTHORIZED, "액세스 토큰이 유효하지 않습니다."),


    // ========================
    // 403 Forbidden
    // ========================
    FORBIDDEN(403_000, HttpStatus.FORBIDDEN, "접속 권한이 없습니다."),
    ACCESS_DENY(403_001, HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
    UNAUTHORIZED_POST_ACCESS(403_002, HttpStatus.FORBIDDEN, "해당 게시글에 접근할 권한이 없습니다."),

    // ========================
    // 404 Not Found
    // ========================
    NOT_FOUND_EMAIL(404_400, HttpStatus.NOT_FOUND, "해당 이메일을 가진 유저가 없습니다"),
    NOT_FOUND_ID(404_401, HttpStatus.NOT_FOUND, "해당 아이디을 가진 유저가 없습니다"),
    USER_NOT_FOUND_IN_COOKIE(404_402, HttpStatus.NOT_FOUND, "쿠키에서 사용자 정보를 찾을 수 없습니다."),

    // ========================
    // 500 Internal Server Error
    // ========================
    INTERNAL_SERVER_ERROR_SECURITY(500_000, HttpStatus.INTERNAL_SERVER_ERROR, "인증 과정에서 서버 오류가 발생했습니다.");

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
