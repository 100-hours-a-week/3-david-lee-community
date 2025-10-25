package bootcamp.kakao.community.common.logging;

import bootcamp.kakao.community.common.util.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpLogUtil {

    private final HttpUtil httpUtil;

    /// HTTP 대한 로그를 찍는 것
    public void logHttpRequest(HttpServletRequest httpServletRequest, String type) {

        /// 바디
        var body = httpServletRequest.getContentType();

        /// 헤더에서 요청 정보 가져오기
        var clientInfo = httpUtil.getClientInfo(httpServletRequest);

        /// 로그
        log.info("{} : {}, [{}], {}, {} ,{}", type, clientInfo.ip(), clientInfo.httpMethod(), clientInfo.uri(), clientInfo.userName(), body);

    }

}
