package bootcamp.kakao.community.platform.images.image.presentation;

import bootcamp.kakao.community.common.aop.HttpSessionId;
import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.platform.images.image.application.ImageUseCase;
import bootcamp.kakao.community.platform.images.image.application.dto.request.ConfirmImageRequest;
import bootcamp.kakao.community.platform.images.image.application.dto.request.PreSignedImageRequest;
import bootcamp.kakao.community.platform.images.image.application.dto.request.temp.ConfirmTempImageRequest;
import bootcamp.kakao.community.platform.images.image.application.dto.request.temp.PreSignedTempImageRequest;
import bootcamp.kakao.community.platform.images.image.application.dto.response.ImageResponse;
import bootcamp.kakao.community.platform.images.image.application.dto.response.PreSignedImageResponse;
import bootcamp.kakao.community.platform.images.image.presentation.swagger.ImageApiSpec;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/images")
@RequiredArgsConstructor
public class ImageApi implements ImageApiSpec {

    private final ImageUseCase service;

    /**
     * 회원가입에서 사용하는 단일 업로드용 URL 발급
     */
    @PostMapping("/temp")
    public ApiResponse<PreSignedImageResponse> tempUpload(
            @RequestBody @Valid PreSignedTempImageRequest request) throws IOException {

        /// 서비스
        PreSignedImageResponse response = service.uploadTemporaryImage(request);

        /// 응답 리턴
        return ApiResponse.created(response);
    }


    /**
     * 여러 개의 파일 저장 임시 업로드 URL 발급
     */
    @PostMapping
    public ApiResponse<List<PreSignedImageResponse>> upload(
            @RequestBody @Valid PreSignedImageRequest request,
            @HttpSessionId Long userId) throws IOException {

        /// 서비스
        List<PreSignedImageResponse> response = service.uploadImages(request);

        /// 응답 리턴
        return ApiResponse.created(response);
    }


    /**
     * 회원가입에서 사용하는 임시 이미지 발급한 것을 S3에 저장함
     */
    @PatchMapping("/temp")
    public ApiResponse<ImageResponse> confirm(
            @RequestBody @Valid ConfirmTempImageRequest request) throws IOException {

        /// 서비스
        var response = service.confirmTempImage(request);

        /// 응답 리턴
        return ApiResponse.ok(response);

    }


    /**
     * 여러 개의 파일을 S3에 저장
     */
    @PatchMapping
    public ApiResponse<List<ImageResponse>> confirm(
            @RequestBody @Valid ConfirmImageRequest request,
            @HttpSessionId Long userId) throws IOException {

        /// 서비스
        var response = service.confirmImages(request, userId);

        /// 응답 리턴
        return ApiResponse.ok(response);
    }

}
