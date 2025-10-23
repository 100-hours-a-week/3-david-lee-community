package bootcamp.kakao.community.platform.posts.post.domain.entity;

import bootcamp.kakao.community.platform.BaseTimeEntity;
import bootcamp.kakao.community.platform.posts.category.domain.entity.Category;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 1000)
    private String thumbnailKey;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Embedded
    private PostStat postStat;

    @Column(nullable = false)
    private boolean deleted;


    /// 빌더 생성자
    @Builder
    protected Post(User user, Category category, String title, String content, String thumbnailKey) {

        /// 예외 처리
        if (user == null) {
            throw new IllegalArgumentException("작성자(user)는 필수입니다.");
        }
        if (category == null) {
            throw new IllegalArgumentException("카테고리(category)는 필수입니다.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목(title)은 비어있을 수 없습니다.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용(content)은 비어있을 수 없습니다.");
        }

        this.user = user;
        this.category = category;
        this.title = title;
        this.content = content;
        this.thumbnailKey = thumbnailKey;
        this.deleted = false;
        this.postStat = new PostStat();
    }

    /// 정적 팩토리 메서드
    public static Post of(User user, Category category, String title, String content, String thumbnailKey) {
        return Post.builder()
                .user(user)
                .category(category)
                .title(title)
                .content(content)
                .thumbnailKey(thumbnailKey)
                .build();
    }

    /// 비즈니스 로직
    public void delete() {
        this.deleted = true;
    }

    /// 수정
    public void update(String title, String content, String thumbnailKey) {

        if (title != null) {
            /// 제목을 수정할 내용이 존재한다면,
            this.title = title;
        }
        if (content != null) {
            /// 내용을 수정할 내용이 존재한다면,
            this.content = content;
        }

        if (thumbnailKey != null) {
            /// 바꿀 썸네일이 존재한다면,
            this.thumbnailKey = thumbnailKey;
        }
    }


}
