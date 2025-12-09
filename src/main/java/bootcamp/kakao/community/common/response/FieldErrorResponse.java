package bootcamp.kakao.community.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 파라미터 오류에 대한 예외처리 입니다.
 * @param field 에러가 발생한 필드명
 * @param message 해당 필드의 에러 메시지
 */
@Schema(name = "[응답][공통] 검증 조건 실패 응답")
public record FieldErrorResponse(
        String field,
        String message) {

    public static FieldErrorResponse of(String field, String message) {
        return new FieldErrorResponse(field, message);
    }
}
