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

    // ========================
    // 401 Unauthorized
    // ========================
    ACCESS_TOKEN_EXPIRED(401_000, HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다."),
    ACCESS_TOKEN_INVALID(401_001, HttpStatus.UNAUTHORIZED, "액세스 토큰이 유효하지 않습니다."),
    ACCESS_TOKEN_UNSUPPORTED(401_002, HttpStatus.UNAUTHORIZED, "액세스 토큰이 지원하지 않는 형식입니다."),
    ACCESS_TOKEN_MALFORMED(401_003, HttpStatus.UNAUTHORIZED, "액세스 토큰의 구조가 깨진 형식입니다."),
    ACCESS_TOKEN_NOT_FOUND(401_004, HttpStatus.UNAUTHORIZED, "액세스 토큰이 존재하지 않습니다."),
    ACCESS_TOKEN_NOT_IP_DEVICE(401_004, HttpStatus.UNAUTHORIZED, "액세스 토큰의 IP와 디바이스 정보가 존재하지 않습니다."),
    ACCESS_TOKEN_INVALID_IP(401_004, HttpStatus.UNAUTHORIZED, "액세스 토큰의 IP가 현재와 일치하지 않습니다."),
    ACCESS_TOKEN_INVALID_DEVICE(401_004, HttpStatus.UNAUTHORIZED, "액세스 토큰의 디바이스 정보가 현재와 일치하지 않습니다."),

    REFRESH_TOKEN_EXPIRED(401_006, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 만료되었습니다."),
    REFRESH_TOKEN_INVALID(401_007, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_UNSUPPORTED(401_008, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 지원하지 않는 토큰 형식입니다."),
    REFRESH_INVALID_LOGIN(401_009, HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 없기에 재로그인이 필요합니다."),
    TOKEN_NOT_FOUND_COOKIE(401_010, HttpStatus.UNAUTHORIZED, "쿠키에 리프레시 토큰이 존재하지 않습니다."),

    // ========================
    // 403 Forbidden
    // ========================
    FORBIDDEN(403_000, HttpStatus.FORBIDDEN, "접속 권한이 없습니다."),
    FORBIDDEN_BLACKLIST(403_001, HttpStatus.FORBIDDEN, "블랙리스트에 등록된 토큰입니다."),
    ACCESS_DENY(403_002, HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
    UNAUTHORIZED_POST_ACCESS(403_003, HttpStatus.FORBIDDEN, "해당 게시글에 접근할 권한이 없습니다."),

    // ========================
    // 404 Not Found
    // ========================
    NOT_FOUND_EMAIL(404_400, HttpStatus.NOT_FOUND, "해당 이메일을 가진 유저가 없습니다"),
    NOT_FOUND_ID(404_401, HttpStatus.NOT_FOUND, "해당 아이디을 가진 유저가 없습니다"),
    USER_NOT_FOUND_IN_COOKIE(404_402, HttpStatus.NOT_FOUND, "쿠키에서 사용자 정보를 찾을 수 없습니다."),
    USER_NOT_FOUND_IN_ACCESS_TOKEN(404_402, HttpStatus.NOT_FOUND, "액세스 토큰에서 사용자 정보를 찾을 수 없습니다."),

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
