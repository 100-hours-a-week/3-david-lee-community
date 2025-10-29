package bootcamp.kakao.community.security.session.filter;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.ErrorCode;
import bootcamp.kakao.community.common.response.code.CommonErrorCode;
import bootcamp.kakao.community.common.response.code.SecurityErrorCode;
import bootcamp.kakao.community.common.util.HttpUtil;
import bootcamp.kakao.community.security.session.application.SessionProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionAuthFilter extends OncePerRequestFilter {

    private final SessionProvider sessionProvider;
    private final HttpUtil httpUtil;

    /// 직렬화
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /// 프리 플라이트는 그냥 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }


        Optional<String> sessionIdOptional = httpUtil.getSessionId(request);
        /// 세션 쿠키가 없으면 인증 패스
        if (sessionIdOptional.isEmpty()) {

            log.info("필터가 필요합니다.");

            filterChain.doFilter(request, response);
            return;
        }

        /// 세션 쿠키가 있으면
        try {

            /// 세션 쿠키가 있다면
            String sessionId = sessionIdOptional.get();

            /// 만료된 세션 키인지 체크
            Long userId = sessionProvider.getUserBySession(sessionId);
            if (userId == null) {
                writeJsonError(response, CommonErrorCode.UNAUTHORIZED);
                return;
            }

            /// 블랙리스트에 없는지 체크
            boolean blacklisted = sessionProvider.isBlacklisted(sessionId);
            if (blacklisted) {
                writeJsonError(response, CommonErrorCode.FORBIDDEN_BLACK_LIST);
                return;
            }

            /// Request 에서 설정
            /// 해당 요청에서 userId가 계속 넘어가도록 설정
            request.setAttribute("userId", userId);

            /// 필터 넘어가기
            filterChain.doFilter(request, response);

        } catch (Exception e) {

            /// 로그 찍기
            log.error(e.getMessage());

            /// 예외 처리 하기
            writeJsonError(response, SecurityErrorCode.INTERNAL_SERVER_ERROR_SECURITY);

        }
    }

    /// JSON 작성하기
    private void writeJsonError(HttpServletResponse response, ErrorCode errorCode) throws IOException {

        /// 응답 처리하기
        CustomException exception = new CustomException(errorCode);
        ApiResponse<Object> apiResponse = ApiResponse.fail(exception);

        /// 응답 바로 보내주기
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /// 응답 JSON
        objectMapper.writeValue(response.getWriter(), apiResponse);

    }

}
