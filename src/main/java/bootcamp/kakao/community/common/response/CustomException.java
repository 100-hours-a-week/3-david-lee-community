package bootcamp.kakao.community.common.response;

import lombok.Getter;

import java.util.List;


/**
 * 응답을 하는 커스텀 예외 클래스입니다.
 * ErrorCode와 필드별 에러 정보를 함께 담아, 일관된 에러 응답을 제공합니다.
 */

@Getter
public class CustomException extends RuntimeException{

    private final ErrorCode errorCode;

    private List<FieldErrorResponse> fieldErrorResponses;


    /// 에러코드만 있는 경우
    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    /// 에러코드와 필드에러가 같이 있는 경우
    public CustomException(ErrorCode errorCode, List<FieldErrorResponse> fieldErrorResponses) {
        this.errorCode = errorCode;
        this.fieldErrorResponses = fieldErrorResponses;
    }

}
