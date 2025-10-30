package bootcamp.kakao.community.common.util;

import org.springframework.stereotype.Component;

@Component
public class KeyUtil {

    /// 공통 키
    private static final String SEPARATOR = ":";
    private static final String DELIMITER = "-";

    /// 게시글
    private static final String POST = "post";
    private static final String VIEW = "view";

    /// 댓글
    private static final String COMMENT = "comment";

    /// 좋아요
    private static final String LIKES = "likes";

    /// 블랙 리스트
    private static final String BLACKLIST = "BL";

    /// JWT
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String ID_CLAIM = "user_id";
    public static final String ROLE_CLAIM = "role";
    public static final String BEARER = "Bearer";
    public static final String AUTHORIZATION = "Authorization";
    public static final String IP_CLAIM = "ip";
    public static final String DEVICE_CLAIM = "device";
    public static final String JWT ="jwt";

    // =====================
    //  합쳐서 사용하는 키 목록
    // =====================

    /// 블랙 리스트 생성 함수
    public static String getBlackList(String token) {
        return BLACKLIST + SEPARATOR + token;
    }

    /// 키 생성 함수
    public static String getRefreshTokenKey(Long userId, String deviceType) {
        return REFRESH_TOKEN + SEPARATOR + userId + SEPARATOR + deviceType;
    }

    /// 게시글 조회수 키 생성 함수
    public static String getPostView(Long postId) {
        return POST + DELIMITER + VIEW + SEPARATOR + postId;
    }

    /// 게시글 댓글수 키 생성 함수
    public static String getPostComment(Long postId) {
        return POST + DELIMITER + COMMENT + SEPARATOR + postId;
    }

    /// 게시글 좋아요수 키 생성 함수
    public static String getPostLike(Long postId) {
        return POST + DELIMITER + LIKES + SEPARATOR + postId;
    }

}
