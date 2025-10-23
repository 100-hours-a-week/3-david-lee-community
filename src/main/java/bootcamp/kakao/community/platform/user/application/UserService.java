package bootcamp.kakao.community.platform.user.application;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.UserErrorCode;
import bootcamp.kakao.community.platform.user.application.dto.*;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import bootcamp.kakao.community.platform.user.domain.repository.UserRepository;
import bootcamp.kakao.community.security.jwt.application.JwtProvider;
import bootcamp.kakao.community.security.jwt.application.dto.JwtTokenResponse;
import bootcamp.kakao.community.security.jwt.application.dto.JwtTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase{

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    /// 유저 이미지
    private final ProfileImageUtil imageService;

    /// 이메일 중복 여부
    @Override
    @Transactional(readOnly = true)
    public boolean checkDuplicateEmail(String email) {
        return repository.existsByEmail(email);
    }

    /// 닉네임 중복 여부
    @Override
    @Transactional(readOnly = true)
    public boolean checkDuplicateNickName(String nickName) {
        return repository.existsByNickname(nickName);
    }


    /// 회원가입, 회원가입 후 바로 이용가능하도록 토큰 발급
    @Override
    @Transactional
    public JwtTokenResponse signUp(SignUpRequest request, String deviceType) {

        /// 이메일 중복체크 및 예외처리
        boolean duplicateEmail = checkDuplicateEmail(request.email());
        if (duplicateEmail) {
            throw new CustomException(UserErrorCode.CONFLICT_DUPLICATE_EMAIL);
        }

        /// 닉네임 중복체크 및 예외처리
        boolean duplicateNickName = checkDuplicateNickName(request.nickname());
        if (duplicateNickName) {
            throw new CustomException(UserErrorCode.CONFLICT_DUPLICATE_NICKNAME);
        }

        /// 요청한 비밀번호 값이 같은지
        if (!request.confirmPassword().equals(request.password())) {
            throw new CustomException(UserErrorCode.BAD_REQUEST_EQUAL_PASSWORD);
        }

        /// 유저 저장
        var requestUser = User.of(request.name(), null, request.nickname(), request.email(), passwordEncoder.encode(request.password()));
        User user = repository.save(requestUser);

        /// 이미지가 있다면 더티체킹 수정 (관심사 분리)
        imageService.assignAndConfirmProfileImage(user, request.imageKey());

        /// 로그인했다면, JWT 발급하기
        var jwtRequest = JwtTokenRequest.from(user);

        String accessToken = jwtProvider.createAccessToken(jwtRequest);
        String refreshToken = jwtProvider.createRefreshToken(deviceType, jwtRequest);

        return JwtTokenResponse.of(accessToken, refreshToken);

    }

    /// 회원탈퇴
    @Override
    @Transactional
    public void withdraw(Long userId) {

        /// 영속성 컨테이너에 값 불러오기
        User user = loadUser(userId);

        /// 더티체킹으로 값 수정하기
        user.delete();
    }

    /// 마이페이지 정보 조회
    @Override
    @Transactional(readOnly = true)
    public MyPageResponse getUser(Long userId) {

        /// 유저 예외처리
        User user = loadUser(userId);

        /// 리턴
        return MyPageResponse.from(user);
    }

    /// 타 유저 조회
    @Override
    @Transactional(readOnly = true)
    public UserResponse getOtherUser(Long userId) {

        /// 유저 예외처리
        User user = loadUser(userId);

        /// 리턴
        return UserResponse.from(user);
    }

    /// 유저 정보 수정 (더티체킹)
    @Override
    @Transactional
    public void updateUser(UserUpdateRequest request, Long userId) {

        /// 유저 예외처리 및 영속성 컨테이너에 등록
        User user = loadUser(userId);

        /// 이미지 수정
        imageService.updateImage(user, request.imageKey());

        /// 닉네임 수정
        user.updateNickname(request.nickname());

    }

    /// 비밀번호 초기화 (더티체킹)
    @Override
    @Transactional
    public void updatePassword(Long userId, PwUpdateRequest request) {

        /// 유저 예외처리 및 영속성 컨테이너에 등록
        User user = loadUser(userId);

        /// 기존 값과 비교해서 변경하기
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new CustomException(UserErrorCode.BAD_REQUEST_OLD_PASSWORD);
        }

        /// 변경할 값이 동일한 지 체크
        /// 요청한 비밀번호 값이 같은지
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new CustomException(UserErrorCode.BAD_REQUEST_EQUAL_PASSWORD);
        }

        /// 새로운 비밀번호로 업데이트 (더티체킹)
        user.updatePassword(passwordEncoder.encode(request.newPassword()));

    }

    // =================
    //  외부 조회 로직
    // =================
    @Override
    public User loadUser(Long userId) {
        return repository.findByIdAndDeletedIsFalse(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_USER));
    }

}
