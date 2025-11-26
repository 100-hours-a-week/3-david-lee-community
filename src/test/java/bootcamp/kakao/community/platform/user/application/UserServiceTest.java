package bootcamp.kakao.community.platform.user.application;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.UserErrorCode;
import bootcamp.kakao.community.platform.user.application.dto.PwUpdateRequest;
import bootcamp.kakao.community.platform.user.application.dto.SignUpRequest;
import bootcamp.kakao.community.platform.user.application.dto.UserUpdateRequest;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import bootcamp.kakao.community.platform.user.domain.entity.UserRole;
import bootcamp.kakao.community.platform.user.domain.repository.UserRepository;
import bootcamp.kakao.community.security.jwt.application.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private ProfileImageUtil imageService;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .name("홍길동")
                .imageKey("test.jpg")
                .nickname("길동이")
                .email("hong@example.com")
                .password("encodedPassword")
                .role(UserRole.MEMBER)
                .build();
    }

    @Test
    @DisplayName("이메일 중복 체크 - 중복됨")
    void checkDuplicateEmail_Exists() {
        // given
        String email = "hong@example.com";
        given(userRepository.existsByEmail(email)).willReturn(true);

        // when
        boolean result = userService.checkDuplicateEmail(email);

        // then
        assertThat(result).isTrue();
        verify(userRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("이메일 중복 체크 - 중복 안됨")
    void checkDuplicateEmail_NotExists() {
        // given
        String email = "new@example.com";
        given(userRepository.existsByEmail(email)).willReturn(false);

        // when
        boolean result = userService.checkDuplicateEmail(email);

        // then
        assertThat(result).isFalse();
        verify(userRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("닉네임 중복 체크 - 중복됨")
    void checkDuplicateNickName_Exists() {
        // given
        String nickname = "길동이";
        given(userRepository.existsByNickname(nickname)).willReturn(true);

        // when
        boolean result = userService.checkDuplicateNickName(nickname);

        // then
        assertThat(result).isTrue();
        verify(userRepository).existsByNickname(nickname);
    }

    @Test
    @DisplayName("닉네임 중복 체크 - 중복 안됨")
    void checkDuplicateNickName_NotExists() {
        // given
        String nickname = "새닉네임";
        given(userRepository.existsByNickname(nickname)).willReturn(false);

        // when
        boolean result = userService.checkDuplicateNickName(nickname);

        // then
        assertThat(result).isFalse();
        verify(userRepository).existsByNickname(nickname);
    }

    @Test
    @DisplayName("회원가입 - 이메일 중복 시 예외 발생")
    void signUp_DuplicateEmail_ThrowsException() {
        // given
        SignUpRequest request = new SignUpRequest(
                "홍길동",
                null,
                "길동이",
                "hong@example.com",
                "password123",
                "password123"
        );
        given(userRepository.existsByEmail(request.email())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(request, "web"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.CONFLICT_DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("회원가입 - 닉네임 중복 시 예외 발생")
    void signUp_DuplicateNickname_ThrowsException() {
        // given
        SignUpRequest request = new SignUpRequest(
                "홍길동",
                null,
                "길동이",
                "hong@example.com",
                "password123",
                "password123"
        );
        given(userRepository.existsByEmail(request.email())).willReturn(false);
        given(userRepository.existsByNickname(request.nickname())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(request, "web"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.CONFLICT_DUPLICATE_NICKNAME);
    }

    @Test
    @DisplayName("회원가입 - 비밀번호 불일치 시 예외 발생")
    void signUp_PasswordMismatch_ThrowsException() {
        // given
        SignUpRequest request = new SignUpRequest(
                "홍길동",
                null,
                "길동이",
                "hong@example.com",
                "password123",
                "password456"
        );
        given(userRepository.existsByEmail(request.email())).willReturn(false);
        given(userRepository.existsByNickname(request.nickname())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.signUp(request, "web"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.BAD_REQUEST_EQUAL_PASSWORD);
    }

    @Test
    @DisplayName("회원탈퇴 - 성공")
    void withdraw_Success() {
        // given
        Long userId = 1L;
        given(userRepository.findByIdAndDeletedIsFalse(userId)).willReturn(Optional.of(testUser));

        // when
        userService.withdraw(userId);

        // then
        assertThat(testUser.isDeleted()).isTrue();
        verify(userRepository).findByIdAndDeletedIsFalse(userId);
    }

    @Test
    @DisplayName("회원탈퇴 - 존재하지 않는 유저")
    void withdraw_UserNotFound_ThrowsException() {
        // given
        Long userId = 999L;
        given(userRepository.findByIdAndDeletedIsFalse(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.withdraw(userId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.NOT_FOUND_USER);
    }

    @Test
    @DisplayName("유저 정보 수정 - 닉네임 변경")
    void updateUser_Nickname_Success() {
        // given
        Long userId = 1L;
        UserUpdateRequest request = new UserUpdateRequest("새닉네임", null);
        given(userRepository.findByIdAndDeletedIsFalse(userId)).willReturn(Optional.of(testUser));

        // when
        userService.updateUser(request, userId);

        // then
        assertThat(testUser.getNickname()).isEqualTo("새닉네임");
        verify(userRepository).findByIdAndDeletedIsFalse(userId);
    }

    @Test
    @DisplayName("비밀번호 변경 - 기존 비밀번호 불일치 시 예외 발생")
    void updatePassword_OldPasswordMismatch_ThrowsException() {
        // given
        Long userId = 1L;
        PwUpdateRequest request = new PwUpdateRequest("wrongOldPassword", "newPassword123", "newPassword123");
        given(userRepository.findByIdAndDeletedIsFalse(userId)).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(request.oldPassword(), testUser.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.BAD_REQUEST_OLD_PASSWORD);
    }

    @Test
    @DisplayName("비밀번호 변경 - 새 비밀번호 불일치 시 예외 발생")
    void updatePassword_NewPasswordMismatch_ThrowsException() {
        // given
        Long userId = 1L;
        PwUpdateRequest request = new PwUpdateRequest("oldPassword", "newPassword123", "newPassword456");
        given(userRepository.findByIdAndDeletedIsFalse(userId)).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(request.oldPassword(), testUser.getPassword())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.BAD_REQUEST_EQUAL_PASSWORD);
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    void updatePassword_Success() {
        // given
        Long userId = 1L;
        String oldPassword = "oldPassword";
        String newPassword = "newPassword123";
        PwUpdateRequest request = new PwUpdateRequest(oldPassword, newPassword, newPassword);

        given(userRepository.findByIdAndDeletedIsFalse(userId)).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(request.oldPassword(), testUser.getPassword())).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn("encodedNewPassword");

        // when
        userService.updatePassword(userId, request);

        // then
        assertThat(testUser.getPassword()).isEqualTo("encodedNewPassword");
        verify(passwordEncoder).encode(newPassword);
    }

    @Test
    @DisplayName("유저 조회 - 존재하지 않는 유저")
    void loadUser_NotFound_ThrowsException() {
        // given
        Long userId = 999L;
        given(userRepository.findByIdAndDeletedIsFalse(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.loadUser(userId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.NOT_FOUND_USER);
    }
}
