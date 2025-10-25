package bootcamp.kakao.community.platform.images.image.application;

import bootcamp.kakao.community.platform.images.image.application.dto.request.ConfirmImageRequest;
import bootcamp.kakao.community.platform.images.image.application.dto.request.PreSignedImageRequest;
import bootcamp.kakao.community.platform.images.image.application.dto.response.ImageResponse;
import bootcamp.kakao.community.platform.images.image.application.dto.response.PreSignedImageResponse;
import bootcamp.kakao.community.platform.images.image.application.dto.request.temp.ConfirmTempImageRequest;
import bootcamp.kakao.community.platform.images.image.application.dto.request.temp.PreSignedTempImageRequest;
import bootcamp.kakao.community.platform.images.image.domain.entity.Image;

import java.io.IOException;
import java.util.List;

public interface ImageUseCase {

    // =================
    //  퍼블릭 로직
    // =================

    /// 여러 사진 저장하기
    List<PreSignedImageResponse> uploadImages(PreSignedImageRequest req) throws IOException;

    /// 회원가입을 위한 임시 저장소
    PreSignedImageResponse uploadTemporaryImage(PreSignedTempImageRequest req) throws IOException;

    /// 임시 이미지를 확정하는 메서드
    ImageResponse confirmTempImage(ConfirmTempImageRequest request) throws IOException;

    /// 여러 이미지를 확정하는 메서드
    List<ImageResponse> confirmImages(ConfirmImageRequest request, Long userId) throws IOException;

    // =================
    //  외부 로직
    // =================

    /// 단일 이미지 가져오기
    Image getImage(String url);

    /// 여러개 이미지 가져오기
    List<Image> getImage(List<String> urls);
}
