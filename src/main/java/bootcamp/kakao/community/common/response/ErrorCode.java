package bootcamp.kakao.community.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 전역에서 사용하는 에러 코드 Enum입니다.
 * 각 에러는 고유 코드, HTTP 상태, 메시지를 포함합니다.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {


    // ========================
    // 0~1000 : 공통 및 보안 에러
    // ========================

    // ========================
    // 400 Bad Request
    // ========================
    BAD_REQUEST(400_000, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_INPUT(400_001, HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    NULL_VALUE(400_002, HttpStatus.BAD_REQUEST, "Null 값이 들어왔습니다."),

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
    NOT_FOUND_END_POINT(404_000, HttpStatus.NOT_FOUND, "요청한 대상이 존재하지 않습니다."),
    USER_NOT_FOUND(404_001, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_NOT_FOUND_IN_COOKIE(404_002, HttpStatus.NOT_FOUND, "쿠키에서 사용자 정보를 찾을 수 없습니다."),
    POST_NOT_FOUND(404_003, HttpStatus.NOT_FOUND, "요청한 게시글을 찾을 수 없습니다."),
    POST_TYPE_NOT_FOUND(404_004, HttpStatus.NOT_FOUND, "게시글 타입을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(404_005, HttpStatus.NOT_FOUND, "요청한 댓글을 찾을 수 없습니다."),

    // ========================
    // 409 Conflict
    // ========================
    DUPLICATE_EMAIL(409_001, HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),


    // ========================
    // 500 Internal Server Error
    // ========================
    INTERNAL_SERVER_ERROR(500_000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    INTERNAL_S3_ERROR(500_001, HttpStatus.INTERNAL_SERVER_ERROR, "AWS S3 설정이 잘못되었습니다. 속성을 확인하세요."),
    INTERNAL_S3_URL_ERROR(500_001, HttpStatus.INTERNAL_SERVER_ERROR, "AWS S3 PreSignedURL 설정이 잘못되었습니다. 속성을 확인하세요."),
    BAD_PARAMETER(999, HttpStatus.BAD_REQUEST, "요청 파라미터에 문제가 존재합니다.");


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
