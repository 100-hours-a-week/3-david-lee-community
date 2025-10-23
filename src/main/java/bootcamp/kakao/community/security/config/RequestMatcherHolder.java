package bootcamp.kakao.community.security.config;

import bootcamp.kakao.community.platform.user.domain.entity.UserRole;
import io.micrometer.common.lang.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpMethod.*;

@Component
public class RequestMatcherHolder {

    private static final List<RequestInfo> REQUEST_INFO_LIST = List.of(

            // 공통
            new RequestInfo(OPTIONS, "/**", null),
            new RequestInfo(GET, "/", null),

            /// 인증
            new RequestInfo(PUT, "/v1/auth", null),     /// 재발급
            new RequestInfo(POST, "/v1/auth", null),     /// 로그인
            new RequestInfo(DELETE, "/v1/auth", UserRole.MEMBER),     /// 로그아웃

            /// 카테고리
            new RequestInfo(PUT, "/v1/category", null),     /// 조회
            new RequestInfo(POST, "/v1/category", UserRole.ADMIN),     /// 생성
            new RequestInfo(DELETE, "/v1/category", UserRole.ADMIN),     /// 삭제

            /// 게시글
            new RequestInfo(GET, "/v1/posts/**", null),     /// 목록조회
            new RequestInfo(PUT, "/v1/posts", UserRole.MEMBER),     /// 삭제
            new RequestInfo(GET, "/v1/posts", null),        /// 상세 조회
            new RequestInfo(POST, "/v1/posts", UserRole.MEMBER),     /// 작성
            new RequestInfo(PATCH, "/v1/posts", UserRole.MEMBER),     /// 수정

            /// 이미지
            new RequestInfo(POST, "/v1/images", UserRole.MEMBER),     /// 이미지 주소
            new RequestInfo(PATCH, "/v1/images", UserRole.MEMBER),     /// 이미지 주소
            new RequestInfo(POST, "/v1/images/temp", null),     /// 회원가입용
            new RequestInfo(PATCH, "/v1/images/temp", null),     /// 회원가입용

            /// 유저
            new RequestInfo(PUT, "/v1/users", UserRole.MEMBER),              /// 회원탈퇴
            new RequestInfo(POST, "/v1/users", null),                /// 회원가입
            new RequestInfo(PUT, "/v1/users/password", UserRole.MEMBER),     /// 비밀번호 수정
            new RequestInfo(GET, "/v1/users/mypage", UserRole.MEMBER),       /// 내 정보
            new RequestInfo(PUT, "/v1/users/mypage", UserRole.MEMBER),       /// 내 정보 수정
            new RequestInfo(GET, "/v1/users/{userId}", null),        /// 다른 정보
            new RequestInfo(GET, "/v1/users/nickname", null),        /// 닉네임 중복 여부
            new RequestInfo(GET, "/v1/users/email", null),           /// 이메일 중복 여부

            /// 댓글
            new RequestInfo(GET, "/v1/comments", null),                 /// 댓글조회
            new RequestInfo(PUT, "/v1/comments", UserRole.MEMBER),              /// 댓글삭제
            new RequestInfo(POST, "/v1/comments", UserRole.MEMBER),              /// 댓글작성
            new RequestInfo(PATCH, "/v1/comments", UserRole.MEMBER),              /// 댓글수정

            /// 신고
            new RequestInfo(POST, "/v1/report", UserRole.MEMBER),              /// 신고

            /// 좋아요
            new RequestInfo(POST, "/v1/posts/likes", UserRole.MEMBER),              /// 좋아요
            new RequestInfo(DELETE, "/v1/posts/likes", UserRole.MEMBER),            /// 좋아요 취소


            // static resources
            new RequestInfo(GET, "/docs/**", null),
            new RequestInfo(GET, "/*.ico", null),
            new RequestInfo(GET, "/resources/**", null),
            new RequestInfo(GET, "/style.css", null),
            new RequestInfo(GET, "/index.html", null),
            new RequestInfo(GET, "/error", null),

            // Swagger UI 및 API 문서 관련 요청
            new RequestInfo(GET, "/v3/api-docs/**", null),
            new RequestInfo(GET, "/swagger-ui/**", null),
            new RequestInfo(GET, "/swagger-resources/**", null),
            new RequestInfo(GET, "/webjars/**", null),
            new RequestInfo(GET, "/swagger-ui.html", null),

            // 정적 아이콘 요청
            new RequestInfo(GET, "/favicon.ico", null),
            new RequestInfo(GET, "/apple-touch-icon.png", null)

    );





    private final ConcurrentHashMap<String, RequestMatcher> reqMatcherCacheMap = new ConcurrentHashMap<>();

    /// 최소 권한이 주어진 요청에 대한 RequestMatcher 반환
    public RequestMatcher getRequestMatchersByMinRole(@Nullable UserRole minRole) {
        var key = getKeyByRole(minRole);
        return reqMatcherCacheMap.computeIfAbsent(key, k ->
                new OrRequestMatcher(REQUEST_INFO_LIST.stream()
                        .filter(reqInfo -> isAccessible(reqInfo.minRole(), minRole))
                        .map(reqInfo -> new AntPathRequestMatcher(reqInfo.pattern(),
                                reqInfo.method().name()))
                        .toArray(AntPathRequestMatcher[]::new)));
    }

    /// 접근 가능한지
    private boolean isAccessible(@Nullable UserRole requiredRole, @Nullable UserRole currentRole) {
        if (requiredRole == null) return true; // 누구나 접근 가능
        if (currentRole == null) return false; // 권한 없음
        return currentRole.ordinal() >= requiredRole.ordinal(); // ADMIN이면 MEMBER 포함
    }

    /// 롤 가져오기
    private String getKeyByRole(@Nullable UserRole minRole) {
        return minRole == null ? "VISITOR" : minRole.name();
    }

    /// 역할 가져오기
    private record RequestInfo(HttpMethod method, String pattern, UserRole minRole) {}

}

