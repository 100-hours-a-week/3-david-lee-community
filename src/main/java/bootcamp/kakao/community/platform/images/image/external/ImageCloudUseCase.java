package bootcamp.kakao.community.platform.images.image.external;

import bootcamp.kakao.community.platform.images.image.application.dto.response.PreSignedImageResponse;

import java.io.IOException;
import java.util.List;

public interface ImageCloudUseCase {

    /// 파일 저장을 위한 presignedURL 제공
    PreSignedImageResponse getUploadPresignedURL(String file) throws IOException;

    /// 파일 저장을 위한 presignedURL 제공
    List<PreSignedImageResponse> getUploadPresignedURL(List<String> files) throws IOException;

    /// key값 바탕으로 URL 제공
    String getUrl(String key);

    /// 파일 삭제
    void deleteFile(String file) throws IOException;

    /// 파일 삭제
    void deleteFile(List<String> files) throws IOException;

}
