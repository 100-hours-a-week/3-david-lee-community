package bootcamp.kakao.community.security.auth.application;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.SecurityErrorCode;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import bootcamp.kakao.community.platform.user.domain.repository.UserRepository;
import bootcamp.kakao.community.security.auth.application.dto.LoginRequest;
import bootcamp.kakao.community.security.session.application.SessionProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    /// 패스워드 암호화
    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    /// 세션 서비스
    private final SessionProvider sessionProvider;

    /**
     * 세션을 통한 로그인
     * @param request   로그인 DTO
     * @return  세션 ID 응답
     */
    @Override
    @Transactional
    public String login(LoginRequest request) {

        /// DB 검증
        User user = repository.findByEmailAndDeletedFalse(request.email())
                .orElseThrow(() -> new CustomException(SecurityErrorCode.NOT_FOUND_EMAIL));

        /// 패스워드 비교
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(SecurityErrorCode.BAD_REQUEST_LOGIN);
        }

        /// 세션 아이디 생성
        return sessionProvider.createSessionId(user);
    }

    /**
     * 세션을 통한 로그아웃
     * @param sessionId 세션 ID
     */
    @Override
    @Transactional
    public void logout(Optional<String> sessionId) {

        /// 세션 키가 없다면 예외 발생
        if(sessionId.isEmpty()){
            throw new CustomException(SecurityErrorCode.BAD_REQUEST_SESSION);
        }

        /// 기존 세션을 블랙리스트에 추가하기
        sessionProvider.addBlacklist(sessionId.get());

        /// 로그아웃 하기 & 세션 서비스에서 지우기
        sessionProvider.removeUserBySession(sessionId.get());

    }
}
