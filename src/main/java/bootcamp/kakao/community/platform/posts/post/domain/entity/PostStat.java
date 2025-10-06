package bootcamp.kakao.community.platform.posts.post.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

/**
 * Embeddable로 구현한 후, 레디스의 값으로서 추가구현해서 사용하는 걸로
 */
@Embeddable
@Getter
public class PostStat {

    @Column(nullable = false)
    private long viewCount;

    @Column(nullable = false)
    private long commentCount;

    @Column(nullable = false)
    private long likeCount;

    /// 기본 값으로 생성자
    protected PostStat() {
        this.viewCount = 0;
        this.commentCount = 0;
        this.likeCount = 0;
    }

    /// 비즈니스 로직
    /// 조회수 수정
    public void updateViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    /// 전부 수정
    public void updateStat(long commentCount, long likeCount, long viewCount) {
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
    }

}
