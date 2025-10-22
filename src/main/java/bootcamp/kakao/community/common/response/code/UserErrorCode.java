package bootcamp.kakao.community.common.response.code;

import bootcamp.kakao.community.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 유저 예외처리 클래스입니다.
 */
@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

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
    NOT_FOUND_USER(404_001, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // ========================
    // 409 Conflict
    // ========================
    CONFLICT_DUPLICATE_NICKNAME(409_000, HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    CONFLICT_DUPLICATE_EMAIL(409_001, HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");

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
