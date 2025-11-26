package bootcamp.kakao.community.platform.user.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User 엔티티 테스트")
class UserTest {

    @Test
    @DisplayName("User 생성 - 정적 팩토리 메서드")
    void createUser() {
        // given
        String name = "홍길동";
        String imageKey = "profile.jpg";
        String nickname = "길동이";
        String email = "hong@example.com";
        String password = "password123";

        // when
        User user = User.of(name, imageKey, nickname, email, password);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getImageKey()).isEqualTo(imageKey);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRole()).isEqualTo(UserRole.MEMBER);
        assertThat(user.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("User 생성 - 빌더 패턴")
    void createUserWithBuilder() {
        // given & when
        User user = User.builder()
                .name("김철수")
                .imageKey("image.jpg")
                .nickname("철수")
                .email("kim@example.com")
                .password("pass1234")
                .role(UserRole.ADMIN)
                .build();

        // then
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("김철수");
        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(user.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("User 삭제 - delete 메서드 호출")
    void deleteUser() {
        // given
        User user = User.of("홍길동", null, "길동이", "hong@example.com", "password123");
        assertThat(user.isDeleted()).isFalse();

        // when
        user.delete();

        // then
        assertThat(user.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("User 이미지 업데이트")
    void updateImage() {
        // given
        User user = User.of("홍길동", "old.jpg", "길동이", "hong@example.com", "password123");
        String newImageKey = "new.jpg";

        // when
        user.updateImage(newImageKey);

        // then
        assertThat(user.getImageKey()).isEqualTo(newImageKey);
    }

    @Test
    @DisplayName("User 이미지 업데이트 - null인 경우 변경하지 않음")
    void updateImageWithNull() {
        // given
        User user = User.of("홍길동", "old.jpg", "길동이", "hong@example.com", "password123");
        String originalImageKey = user.getImageKey();

        // when
        user.updateImage(null);

        // then
        assertThat(user.getImageKey()).isEqualTo(originalImageKey);
    }

    @Test
    @DisplayName("User 닉네임 업데이트")
    void updateNickname() {
        // given
        User user = User.of("홍길동", null, "길동이", "hong@example.com", "password123");
        String newNickname = "새닉네임";

        // when
        user.updateNickname(newNickname);

        // then
        assertThat(user.getNickname()).isEqualTo(newNickname);
    }

    @Test
    @DisplayName("User 닉네임 업데이트 - null인 경우 변경하지 않음")
    void updateNicknameWithNull() {
        // given
        User user = User.of("홍길동", null, "길동이", "hong@example.com", "password123");
        String originalNickname = user.getNickname();

        // when
        user.updateNickname(null);

        // then
        assertThat(user.getNickname()).isEqualTo(originalNickname);
    }

    @Test
    @DisplayName("User 비밀번호 업데이트")
    void updatePassword() {
        // given
        User user = User.of("홍길동", null, "길동이", "hong@example.com", "password123");
        String newPassword = "newPassword456";

        // when
        user.updatePassword(newPassword);

        // then
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }
}
