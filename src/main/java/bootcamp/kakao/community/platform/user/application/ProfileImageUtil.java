package bootcamp.kakao.community.platform.user.application;

import bootcamp.kakao.community.platform.images.image.application.ImageUseCase;
import bootcamp.kakao.community.platform.images.image.domain.entity.Image;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProfileImageUtil {

    /// 이미지 서비스 호출
    private final ImageUseCase imageService;

    /// 이미지 확정
    @Transactional
    public void assignAndConfirmProfileImage(User user, String imageUrl) {

        /// 이미지가 존재한다면,
        if (imageUrl != null) {

            /// 이미지가 존재하는지 확인 및 매핑시키기 (트랜잭션 & 영속성 컨텍스트)
            /// 여기서 에러가 나면, 회원가입 전부 롤백
            Image image = imageService.getImage(imageUrl);

            /// 유저 프로필 수정
            user.updateImage(imageUrl);

            /// 이미지 상태를 확정 (더티체킹)
            image.confirm(user);

        }
    }

    /// 이미지 수정하기
    @Transactional
    public void updateImage(User user, String imageUrl) {

        /// 새로운 프로필 이미지
        String profileImageUrl = null;

        /// 기존 이미지 삭제 처리
        if (user.getImageUrl() != null) {
            Image oldImage = imageService.getImage(user.getImageUrl());
            oldImage.unConfirm();   /// 더티체킹으로 삭제처리
        }

        if (imageUrl != null) {
            /// 새로 넣을 이미지가 존재하는지 체크
            profileImageUrl = imageService.getImage(imageUrl).getKey();
        }

        /// 존재한다면, 새롭게 수정 더티체킹
        user.updateImage(profileImageUrl);
    }

}
