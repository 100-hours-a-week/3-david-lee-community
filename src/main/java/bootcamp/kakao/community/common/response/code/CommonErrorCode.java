package bootcamp.kakao.community.common.response.code;

import bootcamp.kakao.community.common.response.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 공통 예외처리 클래스입니다.
 */
@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {


    // ========================
    // 400 Bad Request
    // ========================
    BAD_REQUEST(400_000, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_INPUT(400_001, HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    NULL_VALUE(400_002, HttpStatus.BAD_REQUEST, "Null 값이 들어왔습니다."),
    BAD_PARAMETER(400_003, HttpStatus.BAD_REQUEST, "요청 파라미터에 문제가 존재합니다."),

    // ========================
    // 401 Unauthorized
    // ========================
    UNAUTHORIZED(401_000, HttpStatus.UNAUTHORIZED, "인증 문제가 발생했습니다."),

    // ========================
    // 403 Forbidden
    // ========================
    FORBIDDEN(403_000, HttpStatus.FORBIDDEN, "접속 권한이 없습니다."),

    // ========================
    // 404 Not Found
    // ========================
    NOT_FOUND(404_000, HttpStatus.NOT_FOUND, "요청한 대상이 존재하지 않습니다."),


    // ========================
    // 409 Conflict
    // ========================
    CONFLICT(409_000, HttpStatus.CONFLICT, "중복된 항목이 존재합니다."),

    // ========================
    // 500 Internal Server Error
    // ========================
    INTERNAL_SERVER_ERROR(500_000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");


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
