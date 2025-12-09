package bootcamp.kakao.community.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * API 응답을 표준화하기 위한 레코드 클래스입니다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "[응답][공통] 공통 응답 객체")
public record ApiResponse<T>(
        @JsonIgnore
        HttpStatus httpStatus,
        boolean success,
        Integer code,
        String message,
        @Nullable T data,
        @Nullable List<FieldErrorResponse> error
) {

    // 성공 응답 생성 (200 OK)
    public static <T> ApiResponse<T> ok(@Nullable final T data) {
        return new ApiResponse<>(HttpStatus.OK, true, HttpStatus.OK.value(), "호출이 성공적으로 완료되었습니다.", data, null);
    }

    // 생성 성공 응답 (201 Created)
    public static <T> ApiResponse<T> created() {
        return new ApiResponse<>(HttpStatus.CREATED, true, HttpStatus.CREATED.value(), "성공적으로 생성되었습니다.", null, null);
    }

    // 생성 성공 응답 (201 Created)
    public static <T> ApiResponse<T> created(final T data) {
        return new ApiResponse<>(HttpStatus.CREATED, true, HttpStatus.CREATED.value(), "성공적으로 생성되었습니다.", data, null);
    }

    // 수정 성공 응답 (204 No Content)
    public static <T> ApiResponse<T> updated() {
        return new ApiResponse<>(HttpStatus.NO_CONTENT, true, HttpStatus.NO_CONTENT.value(), "성공적으로 수정되었습니다.", null, null);
    }

    // 삭제 성공 응답 (204 No Content)
    public static <T> ApiResponse<T> deleted() {
        return new ApiResponse<>(HttpStatus.NO_CONTENT, true, HttpStatus.NO_CONTENT.value(), "성공적으로 삭제 되었습니다.", null, null);
    }

    // 실패 응답 생성
    public static <T> ApiResponse<T> fail(final CustomException e) {
        return new ApiResponse<>(e.getErrorCode().getHttpStatus(), false, e.getErrorCode().getCode(), e.getErrorCode().getMessage(), null, e.getFieldErrorResponses());
    }
}

