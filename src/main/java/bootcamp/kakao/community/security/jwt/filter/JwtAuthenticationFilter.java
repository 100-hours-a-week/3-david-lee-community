package bootcamp.kakao.community.security.jwt.filter;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.ErrorCode;
import bootcamp.kakao.community.common.response.code.SecurityErrorCode;
import bootcamp.kakao.community.common.util.HttpUtil;
import bootcamp.kakao.community.security.jwt.application.JwtBlackListValidator;
import bootcamp.kakao.community.security.jwt.application.JwtValidator;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /// JWT 체크
    private final JwtValidator jwtValidator;

    /// 블랙리스트 체크
    private final JwtBlackListValidator blackListValidator;

    /// HTTP 유틸
    private final HttpUtil httpUtil;

    /// JSON 출력
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /// OPTIONS 필터에서 타지않도록 넣는다.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            /// 액세스 토큰 추출
            Optional<String> accessTokenOptional = httpUtil.getAccessToken(request);
            var requestInfo = httpUtil.getRequestInfo(request);

            /// 토큰이 존재할 때만 인증 처리
            if (accessTokenOptional.isPresent()) {
                String accessToken = accessTokenOptional.get();

                /// 블랙 리스트에 포함되었는 지 파악 (상위에서 예외 처리하도록, 내부에서 던짐)
                blackListValidator.checkBlackList(accessToken);

                /// 적절한 토큰인 지 파악하고, 유저를 체크 (상위에서 예외 처리하도록, 내부에서 던짐)
                JwtValidator.UserSecurity userSecurity = jwtValidator.validateAccessToken(accessToken);

                /// 기존 요청과 달라진 점이 존재하는지 체크 (상위에서 예외 처리하도록, 내부에서 던짐)
                jwtValidator.validateIpFromToken(requestInfo.ip(), userSecurity, accessToken);

                /// 다음 요청까지 계속 이어지도록 설정
                /// 인증 객체 설정과 비슷하게끔 ...
                request.setAttribute("userId", userSecurity.userId());
                request.setAttribute("role", userSecurity.userRole());

            }

            /// 토큰이 없거나, 인증에 성공했으면 다음 필터로 진행
            filterChain.doFilter(request, response);

        } catch (CustomException ex) {

            /// 하위 예외를 전부 받기
            writeJsonError(response, ex.getErrorCode());
        } catch (Exception ex) {
            /// 예외처리 핸들러는 스프링에서 작동되는 것이기에 필터에서 로그를 남기기.
            log.info(ex.getMessage(), ex);
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
