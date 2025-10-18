package bootcamp.kakao.community.platform.user.application;

import bootcamp.kakao.community.platform.images.image.application.ImageService;
import bootcamp.kakao.community.platform.images.image.domain.entity.Image;
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

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase{

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;
    private final ImageService imageService;

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
            throw new IllegalArgumentException("이메일이 중복되는 문제입니다.");
        }

        /// 닉네임 중복체크 및 예외처리
        boolean duplicateNickName = checkDuplicateNickName(request.nickname());
        if (duplicateNickName) {
            throw new IllegalArgumentException("닉네임이 중복되는 문제입니다.");
        }

        /// 요청한 비밀번호 값이 같은지
        if (!request.confirmPassword().equals(request.password())) {
            throw new IllegalArgumentException("패스워드가 일치하지 않는 문제입니다.");
        }

        User user;

        /// 이미지가 존재한다면,
        if (request.imageUrl() != null) {

            String imageUrl = request.imageUrl();

            /// 이미지가 존재하는지 확인 및 매핑시키기 (트랜잭션 & 영속성 컨텍스트)
            /// 여기서 에러가 나면, 회원가입 전부 롤백
            Image image = imageService.getImage(imageUrl);

            /// 객체 생성 후, 저장
            var requestUser = User.of(request.name(), imageUrl, request.nickname(), request.email(), passwordEncoder.encode(request.password()));
            user = repository.save(requestUser);

            /// 이미지 상태를 확정 (더티체킹)
            image.confirm(user);

        } else {
            /// 이미지가 없다면,
            /// 객체 생성 후, 저장
            var requestUser = User.of(request.name(), null, request.nickname(), request.email(), passwordEncoder.encode(request.password()));
            user = repository.save(requestUser);

        }

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

        /// 새로운 프로필 이미지
        String profileImageUrl = null;

        /// 기존 이미지 삭제 처리
        if (user.getImageUrl() != null) {
            Image oldImage = imageService.getImage(user.getImageUrl());
            oldImage.unConfirm();   /// 더티체킹으로 삭제처리
        }

        if (request.imageUrl() != null) {
            /// 새로 넣을 이미지가 존재하는지 체크
            profileImageUrl = imageService.getImage(request.imageUrl()).getUrl();
        }

        /// 존재한다면, 새롭게 수정 더티체킹
        user.update(profileImageUrl, request.nickname());

    }

    /// 비밀번호 초기화 (더티체킹)
    @Override
    @Transactional
    public void updatePassword(Long userId, PwUpdateRequest request) {

        /// 유저 예외처리 및 영속성 컨테이너에 등록
        User user = loadUser(userId);

        /// 기존 값과 비교해서 변경하기
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalStateException("기존 비밀번호와 일치하지 않습니다.");
        }

        /// 변경할 값이 동일한 지 체크
        /// 요청한 비밀번호 값이 같은지
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("패스워드가 일치하지 않는 문제입니다.");
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
                .orElseThrow(() -> new NoSuchElementException("해당 아이디가 존재하는 유저가 없습니다."));
    }

}
