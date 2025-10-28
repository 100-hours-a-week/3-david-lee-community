package bootcamp.kakao.community.platform.user.application;

import bootcamp.kakao.community.platform.user.application.dto.*;
import bootcamp.kakao.community.platform.user.domain.entity.User;

public interface UserUseCase {

    /// 이메일 중복 여부
    boolean checkDuplicateEmail(String email);

    /// 이메일 중복 여부
    boolean checkDuplicateNickName(String nickName);

    /// 회원가입
    String signUp(SignUpRequest authRequest);

    /// 회원 탈퇴
    void withdraw(Long userId);

    /// 마이페이지 정보 조회
    MyPageResponse getUser(Long userId);

    /// 타 유저 정보 조회
    UserResponse getOtherUser(Long userId);

    /// 유저 정보 수정
    void updateUser(UserUpdateRequest request, Long userId);

    /// 유저 비밀번호 변경
    void updatePassword(Long userId, PwUpdateRequest request);

    /// 외부 함수
    User loadUser(Long userId);

}
