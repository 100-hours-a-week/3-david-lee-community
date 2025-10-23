package bootcamp.kakao.community.common.response.code;

import bootcamp.kakao.community.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 이미지 예외처리 클래스입니다.
 */
@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {

    // ========================
    // 400 Bad Request
    // ========================
    BAD_REQUEST_CONFIRM(400_100, HttpStatus.BAD_REQUEST, "사용자 정보가 없는 이미지는 확정할 수 없습니다."),
    BAD_REQUEST_UN_CONFIRM(400_100, HttpStatus.BAD_REQUEST, "사용자 정보가 없는 이미지는 취소할 수 없습니다."),

    // ========================
    // 401 Unauthorized
    // ========================

    // ========================
    // 403 Forbidden
    // ========================

    // ========================
    // 404 Not Found
    // ========================
    NOT_FOUND_IMAGE(404_100, HttpStatus.NOT_FOUND, "해당 이미지가 존재하지 않습니다."),

    // ========================
    // 409 Conflict
    // ========================

    // ========================
    // 500 Internal Server Error
    // ========================
    INTERNAL_S3_ERROR(500_101, HttpStatus.INTERNAL_SERVER_ERROR, "AWS S3 설정이 잘못되었습니다. 속성을 확인하세요."),
    INTERNAL_S3_URL_ERROR(500_101, HttpStatus.INTERNAL_SERVER_ERROR, "AWS S3 PreSignedURL 설정이 잘못되었습니다. 속성을 확인하세요.");



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
