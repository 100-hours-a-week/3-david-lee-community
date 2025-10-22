package bootcamp.kakao.community.security.jwt.filter;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.CommonErrorCode;
import bootcamp.kakao.community.common.response.code.SecurityErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFailureHandler implements AuthenticationEntryPoint {


    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        /// 인증 문제 발생시, 401 Error 발생
        CustomException exception = new CustomException(CommonErrorCode.UNAUTHORIZED);

        /// JWT 예외인 경우
        if (authException instanceof JwtAuthenticationException jwtEx) {
            exception = new CustomException(jwtEx.getErrorCode());
        }

        ApiResponse<Object> apiResponse = ApiResponse.fail(exception);

        // 응답 설정
        response.setStatus(apiResponse.httpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 응답
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
