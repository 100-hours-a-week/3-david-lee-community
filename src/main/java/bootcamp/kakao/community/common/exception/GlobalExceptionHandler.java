package bootcamp.kakao.community.common.exception;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.ErrorCode;
import bootcamp.kakao.community.common.response.FieldErrorResponse;
import bootcamp.kakao.community.common.response.code.CommonErrorCode;
import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /// CustomException 에러 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {

        /// 에러 코드
        ErrorCode errorCode = e.getErrorCode();

        /// 로그찍기
        log.error(errorCode.getMessage());

        /// 응답
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(e));
    }

    /// @Valid 파라미터 에러 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<CustomException> handleValidationExceptions(MethodArgumentNotValidException e) {

        /// 에러 이유 로그 찍기
        log.error(e.getMessage());

        /// 파라미터용 예외 코드
        ErrorCode errorCode = CommonErrorCode.BAD_PARAMETER;

        /// 기본 에러 코드로 응답 생성 및 파라미터 담기
        List<FieldErrorResponse> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> FieldErrorResponse.of(error.getField(), error.getDefaultMessage()))
                .toList();

        CustomException exception = new CustomException(errorCode, errors);

        /// 응답
        return ApiResponse.fail(exception);
    }

    /// 레디스 에러 처리 핸들러
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(RedisConnectionFailureException.class)
    public ApiResponse<?> handleRedisConnectionFailureException(RedisConnectionFailureException e) {

        /// 에러 이유 로그 찍기
        log.error(e.getMessage());

        /// 기본 에러 코드로 응답 생성
        ErrorCode errorCode = CommonErrorCode.INTERNAL_REDIS_SERVER_ERROR;
        CustomException exception = new CustomException(errorCode);

        /// 응답
        return ApiResponse.fail(exception);
    }

    /// 값이 없는 내용 에러 처리
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoSuchElementException.class, NoResourceFoundException.class})
    public ApiResponse<?> handleNoSuchException(Exception e) {

        /// 에러 이유 로그 찍기
        log.error(e.getMessage());

        /// 기본 에러 코드로 응답 생성
        ErrorCode errorCode = CommonErrorCode.NOT_FOUND;
        CustomException exception = new CustomException(errorCode);

        /// 응답
        return ApiResponse.fail(exception);
    }

    /// 값이 없는 내용 에러 처리
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NullPointerException.class)
    public ApiResponse<?> handleNullPointerException(NullPointerException e) {

        /// 에러 이유 로그 찍기
        log.error(e.getMessage());

        /// 기본 에러 코드로 응답 생성
        ErrorCode errorCode = CommonErrorCode.NULL_VALUE;
        CustomException exception = new CustomException(errorCode);

        /// 응답
        return ApiResponse.fail(exception);
    }

    /// 값이 없는 내용 에러 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ApiResponse<?> handleIllegalException(Exception e) {

        /// 에러 이유 로그 찍기
        log.error(e.getMessage(), e);

        /// 기본 에러 코드로 응답 생성
        ErrorCode errorCode = CommonErrorCode.BAD_REQUEST;
        CustomException exception = new CustomException(errorCode);

        /// 응답
        return ApiResponse.fail(exception);
    }

    /// JSON 값 에러 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<?> handleJSONException(HttpMessageNotReadableException e) {

        /// 에러 이유 로그 찍기
        log.error(e.getMessage());

        /// 기본 에러 코드로 응답 생성
        ErrorCode errorCode = CommonErrorCode.BAD_REQUEST_JSON;
        CustomException exception = new CustomException(errorCode);

        /// 응답
        return ApiResponse.fail(exception);
    }

    /// 타입 오류
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({UnexpectedTypeException.class, MethodArgumentTypeMismatchException.class})
    public ApiResponse<?> handleJUnexpectedTypeException(Exception e) {

        /// 에러 이유 로그 찍기
        log.error(e.getMessage());

        /// 기본 에러 코드로 응답 생성
        ErrorCode errorCode = CommonErrorCode.BAD_REQUEST_INVALID_INPUT;
        CustomException exception = new CustomException(errorCode);

        /// 응답
        return ApiResponse.fail(exception);
    }


    /// DB 문법 등 관련 문제 발생
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ApiResponse<?> handleInvalidDataAccessResourceUsageException(InvalidDataAccessResourceUsageException e) {

        /// 에러 이유 로그 찍기
        log.error(e.getMessage());

        /// 기본 에러 코드로 응답 생성
        ErrorCode errorCode = CommonErrorCode.INTERNAL_DB_SERVER_ERROR;
        CustomException exception = new CustomException(errorCode);

        /// 응답
        return ApiResponse.fail(exception);
    }

    /// DB 스키마 관련 문제 발생
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({DataIntegrityViolationException.class, DuplicateKeyException.class})
    public ApiResponse<?> handleDataIntegrityViolationException(Exception e) {

        /// 에러 이유 로그 찍기
        log.error(e.getMessage());

        /// 기본 에러 코드로 응답 생성
        ErrorCode errorCode = CommonErrorCode.INTERNAL_DB_SCHEMA_SERVER_ERROR;
        CustomException exception = new CustomException(errorCode);

        /// 응답
        return ApiResponse.fail(exception);
    }

    /// DB 스키마 관련 문제 발생
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(TransientDataAccessException.class)
    public ApiResponse<?> handleTransientDataAccessException(TransientDataAccessException e) {

        /// 에러 이유 로그 찍기
        log.error(e.getMessage());

        /// 기본 에러 코드로 응답 생성
        ErrorCode errorCode = CommonErrorCode.INTERNAL_DB_SERVER_ERROR;
        CustomException exception = new CustomException(errorCode);

        /// 응답
        return ApiResponse.fail(exception);
    }

    /// 최하위 에러 처리 (여기까지는 안오길 ...)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {

        /// 에러 이유 로그 찍기
        log.error(e.getMessage(), e);

        /// 기본 에러 코드로 응답 생성
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        CustomException exception = new CustomException(errorCode);

        /// 응답
        return ApiResponse.fail(exception);
    }



}
