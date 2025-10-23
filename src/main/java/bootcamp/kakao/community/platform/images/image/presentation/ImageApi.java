package bootcamp.kakao.community.platform.images.image.presentation;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.platform.images.image.application.ImageUseCase;
import bootcamp.kakao.community.platform.images.image.application.dto.ImageResponse;
import bootcamp.kakao.community.platform.images.image.application.dto.PreSignedImageRequest;
import bootcamp.kakao.community.platform.images.image.application.dto.PreSignedImageResponse;
import bootcamp.kakao.community.platform.images.image.presentation.swagger.ImageApiSpec;
import bootcamp.kakao.community.security.auth.domain.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @RequestBody @Valid PreSignedImageRequest request) throws IOException {

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
            @RequestBody @Valid List<PreSignedImageRequest> request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) throws IOException {

        /// 서비스
        List<PreSignedImageResponse> response = service.uploadImages(request);

        /// 응답 리턴
        return ApiResponse.created(response);
    }


    /**
     * 회원가입에서 사용하는 임시 이미지 발급한 것을 S3에 저장함
     */
    @PatchMapping("/temp")
    public ApiResponse<ImageResponse> confirm(@RequestParam String key) throws IOException {

        /// 서비스
        var response = service.confirmTempImage(key);

        /// 응답 리턴
        return ApiResponse.ok(response);

    }


    /**
     * 여러 개의 파일을 S3에 저장
     */
    @PatchMapping
    public ApiResponse<List<ImageResponse>> confirm(
            @RequestParam List<String> keys,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) throws IOException {

        /// 서비스
        var responses = service.confirmImages(keys, customUserDetails.getId());

        /// 응답 리턴
        return ApiResponse.ok(responses);

    }

}
