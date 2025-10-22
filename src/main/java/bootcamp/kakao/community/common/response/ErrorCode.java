package bootcamp.kakao.community.common.response;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    Integer getCode();

    String getMessage();

    HttpStatus getHttpStatus();

}
