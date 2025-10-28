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


    // =====================
    //  합쳐서 사용하는 키 목록
    // =====================

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
