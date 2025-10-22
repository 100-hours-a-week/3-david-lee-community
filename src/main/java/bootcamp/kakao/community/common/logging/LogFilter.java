package bootcamp.kakao.community.common.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LogFilter extends OncePerRequestFilter {

    private final HttpLogUtil logUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /// 로그 찍고 넘기기
        logUtil.logHttpRequest(request, LogType.HTTP.getLabel());
        filterChain.doFilter(request, response);
    }
}
