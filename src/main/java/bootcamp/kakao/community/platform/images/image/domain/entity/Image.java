package bootcamp.kakao.community.platform.images.image.domain.entity;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.ImageErrorCode;
import bootcamp.kakao.community.platform.BaseTimeEntity;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "images", indexes = @Index(name = "idx_image_key", columnList = "key"))
public class Image extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "image_key", nullable = false, length = 1000)
    private String key;

    @Column(nullable = false)
    private boolean confirmed = false;

    /// 생성자
    @Builder
    protected Image(User user, String key) {
        this.user = user;
        this.key = key;
        confirmed = false;
    }

    /// 정적 팩토리 메서드
    public static Image of(User user, String key) {
        return Image.builder()
                .user(user)
                .key(key)
                .build();
    }

    /// 임시 생성 정적 팩토리 메서드
    public static Image temporaryOf(String key) {
        return Image.builder()
                .user(null)
                .key(key)
                .build();
    }

    /// 비즈니스 로직
    // 사용한다고 확정하는 메서드
    public void confirm(User user) {
        this.user = user;
        this.confirmed = true;
    }

    // 사용한다고 확정하는 메서드
    public void confirm() {

        /// 예외처리
        if (this.user == null) {
            throw new CustomException(ImageErrorCode.BAD_REQUEST_CONFIRM);
        }

        this.confirmed = true;
    }

    // 사용을 취소하는 메서드
    public void unConfirm() {

        /// 예외처리
        if (this.user == null) {
            throw new CustomException(ImageErrorCode.BAD_REQUEST_UN_CONFIRM);
        }

        /// 사용중이던 것만 취소 가능
        if (this.confirmed) {
            this.confirmed = false;
        }
    }

}
