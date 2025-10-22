package bootcamp.kakao.community.platform.images.image.application;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.code.ImageErrorCode;
import bootcamp.kakao.community.common.response.code.UserErrorCode;
import bootcamp.kakao.community.platform.images.image.application.dto.ImageRequest;
import bootcamp.kakao.community.platform.images.image.application.dto.ImageResponse;
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
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService implements ImageUseCase {

    private final ImageCloudUseCase cloudService;
    private final ImageRepository repository;

    /// 의존성
    private final UserRepository userRepository;


    /// 여러개의 이미지 처리
    @Override
    @Transactional
    public List<ImageResponse> upload(List<ImageRequest> req, Long userId) throws IOException {

        /// 요청한 유저
        User user = loadUser(userId);

        /// 요청한 이미지 목록 추출
        List<String> reqImages = req.stream()
                .map(ImageRequest::imageUrl)
                .toList();

        /// 클라우드 요청
        List<ImageResponse> presignedURLs = cloudService.getUploadPresignedURL(reqImages);

        /// 객체 저장
        List<Image> images = presignedURLs.stream()
                .map(p -> Image.of(user, p.fileName(), p.preSignedUrl()))
                .toList();
        repository.saveAll(images);

        /// 리턴
        return presignedURLs;
    }

    /// 회원가입을 위한 한 장의 이미지 임시 처리
    /// 추후, 확정을 지어야한다.
    @Override
    @Transactional
    public ImageResponse uploadTemporaryImage(ImageRequest req) throws IOException {

        /// 클라우드 요청
        ImageResponse presignedURL = cloudService.getUploadPresignedURL(req.imageUrl());

        /// 객체 저장
        Image image = Image.temporaryOf(req.imageUrl(), presignedURL.preSignedUrl());
        repository.save(image);

        /// 리턴
        return presignedURL;
    }

    /// URL 바탕으로 이미지 객체 조회하기
    @Override
    @Transactional
    public Image getImage(String url) {

        return repository.findByUrl(url)
                .orElseThrow(() -> new CustomException(ImageErrorCode.NOT_FOUND_IMAGE));
    }

    /// URL 목록 바탕으로 이미지 배열 객체 조회하기
    @Override
    @Transactional
    public List<Image> getImage(List<String> urls) {

        /// 값 가져오기
        List<Image> images = repository.findAllByUrlIn(urls);

        /// Map 변환해서 순서 재정렬
        Map<String, Image> imageMap = images.stream()
                .collect(Collectors.toMap(Image::getUrl, i -> i));

        /// List 제공
        return urls.stream()
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
