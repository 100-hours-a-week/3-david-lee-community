package bootcamp.kakao.community.platform.user.domain.entity;

import bootcamp.kakao.community.platform.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users", indexes = @Index(name = "idx_user_email", columnList = "email", unique = true))
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_key", length = 1000)
    private String imageKey;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean deleted;

    /// 빌더 생성자
    @Builder
    protected User(String name, String imageKey, String nickname, String email, String password, UserRole role) {
        this.name = name;
        this.imageKey = imageKey;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role != null ? role : UserRole.MEMBER;
        this.deleted = false;
    }

    /// 정적 팩토리 메서드 (기본 유저)
    public static User of(String name, String imageKey, String nickname, String email, String password) {
        return User.builder()
                .name(name)
                .imageKey(imageKey)
                .nickname(nickname)
                .email(email)
                .password(password)
                .build();
    }

    /// 비즈니스 로직
    /// 삭제
    public void delete() {
        this.deleted = true;
    }

    /// 이미지 업데이트
    public void updateImage(String imageKey) {

        if (imageKey != null) {
            /// 프로필이미지를 수정할 내용이 존재한다면,
            this.imageKey = imageKey;
        }
    }

    /// 닉네임 업데이트
    public void updateNickname(String nickname) {

        if (nickname != null) {
            /// 닉네임을 수정할 내용이 존재한다면,
            this.nickname = nickname;
        }
    }

    /// 비밀번호 업데이트
    public void updatePassword(String passwordHash) {
        this.password = passwordHash;
    }

}
