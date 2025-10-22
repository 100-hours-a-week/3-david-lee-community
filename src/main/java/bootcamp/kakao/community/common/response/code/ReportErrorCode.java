package bootcamp.kakao.community.common.response.code;

import bootcamp.kakao.community.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 신고 예외처리 클래스입니다.
 */
@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements ErrorCode {

    // ========================
    // 400 Bad Request
    // ========================

    // ========================
    // 401 Unauthorized
    // ========================

    // ========================
    // 403 Forbidden
    // ========================

    // ========================
    // 404 Not Found
    // ========================

    // ========================
    // 409 Conflict
    // ========================

    // ========================
    // 500 Internal Server Error
    // ========================
    ;


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
