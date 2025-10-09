package bootcamp.kakao.community.platform.images.image.application;

import bootcamp.kakao.community.platform.images.image.application.dto.ImageRequest;
import bootcamp.kakao.community.platform.images.image.application.dto.ImageResponse;
import bootcamp.kakao.community.platform.images.image.domain.entity.Image;

import java.io.IOException;
import java.util.List;

public interface ImageUseCase {

    /// 여러 사진 저장하기
    List<ImageResponse> upload(List<ImageRequest> req, Long userId) throws IOException;

    /// 회원가입을 위한 임시 저장소
    ImageResponse uploadTemporaryImage(ImageRequest req) throws IOException;

    /// 외부 서비스에서 사용할 로직
    /// 단일 이미지 가져오기
    Image getImage(String url);

    /// 여러개 이미지 가져오기
    List<Image> getImage(List<String> urls);
}
