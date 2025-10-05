package bootcamp.kakao.community.platform.posts.comment.domain.entity;

import bootcamp.kakao.community.platform.BaseTimeEntity;
import bootcamp.kakao.community.platform.posts.post.domain.entity.Post;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "comments", indexes = {@Index(name = "idx_comment_",columnList = "parent_id, deleted")})
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private Comment parent;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean deleted;

    /// 생성자
    @Builder
    protected Comment(Post post, User user, Comment parent, String content) {
        this.post = post;
        this.user = user;
        this.parent = parent;
        this.content = content;
        this.deleted = false;
    }

    /// 정적 팩토리 메서드
    public static Comment of(Post post, User user, Comment parent, String content) {
        return Comment.builder()
                .post(post)
                .user(user)
                .parent(parent)
                .content(content)
                .build();
    }

    /// 비즈니스 로직
    public void delete() {
        this.deleted = true;
    }

    /// 업데이트
    public void update(String content) {

        if (content == null) {
            throw new IllegalArgumentException("수정할 내용이 존재하지 않습니다.");
        }

        this.content = content;
    }
}
