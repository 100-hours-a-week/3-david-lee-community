package bootcamp.kakao.community.common.response.code;

import bootcamp.kakao.community.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 게시글 예외처리 클래스입니다.
 */
@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {

    // ========================
    // 400 Bad Request
    // ========================
    BAD_REQUEST_POST(400_200, HttpStatus.BAD_REQUEST, "게시글 요청 파라미터가 잘못되었습니다."),

    // ========================
    // 403 Forbidden
    // ========================
    FORBIDDEN_POST_EDIT(403_200, HttpStatus.FORBIDDEN, "해당 게시글을 수정/삭제할 권한이 없습니다."),

    // ========================
    // 404 Not Found
    // ========================
    NOT_FOUND_POST(404_200, HttpStatus.NOT_FOUND, "요청한 게시글을 찾을 수 없습니다."),
    NOT_FOUND_POST_TYPE(404_201, HttpStatus.NOT_FOUND, "게시글 타입을 찾을 수 없습니다."),
    NOT_FOUND_COMMENT(404_202, HttpStatus.NOT_FOUND, "요청한 댓글을 찾을 수 없습니다.");

    // ========================
    // 409 Conflict
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
