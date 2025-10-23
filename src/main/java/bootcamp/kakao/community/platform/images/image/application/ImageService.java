package bootcamp.kakao.community.platform.images.image.application;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.ImageErrorCode;
import bootcamp.kakao.community.common.response.code.UserErrorCode;
import bootcamp.kakao.community.common.util.ImageUtil;
import bootcamp.kakao.community.platform.images.image.application.dto.request.ConfirmImageRequest;
import bootcamp.kakao.community.platform.images.image.application.dto.request.PreSignedImageRequest;
import bootcamp.kakao.community.platform.images.image.application.dto.response.ImageResponse;
import bootcamp.kakao.community.platform.images.image.application.dto.response.PreSignedImageResponse;
import bootcamp.kakao.community.platform.images.image.application.dto.request.temp.ConfirmTempImageRequest;
import bootcamp.kakao.community.platform.images.image.application.dto.request.temp.PreSignedTempImageRequest;
import bootcamp.kakao.community.platform.images.image.domain.entity.Image;
import bootcamp.kakao.community.platform.images.image.domain.repository.ImageRepository;
import bootcamp.kakao.community.platform.images.image.external.ImageCloudUseCase;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import bootcamp.kakao.community.platform.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService implements ImageUseCase {

    /// DB 의존성
    private final ImageRepository repository;
    private final UserRepository userRepository;

    /// 클라우드 의존성
    private final ImageCloudUseCase cloudService;

    // =================
    //  퍼블릭 로직
    // =================

    /// 여러 장의 임시저장 URL 발급 (확정 X)
    @Override
    @Transactional
    public List<PreSignedImageResponse> uploadImages(PreSignedImageRequest req) throws IOException {

        /// 요청한 이미지 목록
        List<String> reqImages = req.fileNames();

        /// 클라우드 요청
        return cloudService.getUploadPresignedURL(reqImages);
    }

    /// 회원가입을 위한 한 장의 임시저장 URL 발급 (확정 X)
    @Override
    @Transactional
    public PreSignedImageResponse uploadTemporaryImage(PreSignedTempImageRequest req) throws IOException {

        /// 클라우드 PreSignedURL 요청 후 전달
        return cloudService.getUploadPresignedURL(req.fileName());
    }

    /// 하나의 이미지를 DB 저장하기
    /// 일단은 S3에 올라가는 것이 된다.
    @Override
    @Transactional
    public ImageResponse confirmTempImage(ConfirmTempImageRequest request) throws IOException {

        /// key값으로 임시 이미지 저장하기
        Image reqImage = Image.temporaryOf(request.key());

        /// 이미지 저장하기
        Image image = repository.save(reqImage);

        /// 리턴
        return ImageResponse.from(ImageUtil.getUrlByKey(image.getKey()));
    }
    
    /// 여러 개의 이미지를 DB에 저장하는 로직
    /// 일단은 S3에 올라가는 것이 된다.
    @Override
    @Transactional
    public void confirmImages(ConfirmImageRequest request, Long userId) throws IOException {

        /// 요청한 유저
        User user = loadUser(userId);

        /// key값으로 임시 이미지 객체들 저장하기
        List<Image> reqImages = request.keys().stream()
                .map(key -> Image.of(user, key))
                .toList();

        /// DB에 저장하기
        repository.saveAll(reqImages);
    }

    // =================
    //  외부 로직
    // =================

    /// URL 바탕으로 이미지 객체 조회하기
    @Override
    @Transactional
    public Image getImage(String key) {

        return repository.findByKey(key)
                .orElseThrow(() -> new CustomException(ImageErrorCode.NOT_FOUND_IMAGE));
    }

    /// URL 목록 바탕으로 이미지 배열 객체 조회하기
    @Override
    @Transactional
    public List<Image> getImage(List<String> keys) {

        /// 값 가져오기
        List<Image> images = repository.findAllByKeyIn(keys);

        /// Map 변환해서 순서 재정렬
        Map<String, Image> imageMap = images.stream()
                .collect(Collectors.toMap(Image::getKey, i -> i));

        /// List 제공
        return keys.stream()
                .map(url -> imageMap.getOrDefault(url, null))
                .filter(Objects::nonNull)
                .toList();
    }

    // =============
    //   내부 함수
    // =============

    @Transactional
    protected User loadUser(Long userId) {
        return userRepository.findByIdAndDeletedIsFalse(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_USER));
    }

}
