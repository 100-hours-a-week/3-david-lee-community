package bootcamp.kakao.community.security.jwt.filter;

import bootcamp.kakao.community.common.logging.HttpLogUtil;
import bootcamp.kakao.community.common.logging.LogType;
import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.CommonErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final HttpLogUtil logUtil;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        /// 권한 부족 403 Error
        CustomException exception = new CustomException(CommonErrorCode.FORBIDDEN);
        ApiResponse<Object> apiResponse = ApiResponse.fail(exception);

        /// response 제작
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /// 로그 찍기
        logUtil.logHttpRequest(request, LogType.ERROR_403.getLabel());

        // JSON 응답
        objectMapper.writeValue(response.getWriter(), apiResponse);

    }
}
