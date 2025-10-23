package bootcamp.kakao.community.platform.images.image.presentation.swagger;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.platform.images.image.application.dto.ImageResponse;
import bootcamp.kakao.community.platform.images.image.application.dto.PreSignedImageRequest;
import bootcamp.kakao.community.platform.images.image.application.dto.PreSignedImageResponse;
import bootcamp.kakao.community.security.auth.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Tag(name = "이미지 API", description = "이미지 발급을 위한 API 입니다.")
public interface ImageApiSpec {

    @Operation(
            summary = "파일 업로드용 이미지 URL 발급 API",
            description = "여러개의 파일의 URL 발급 받습니다."
    )
    ApiResponse<List<PreSignedImageResponse>> upload(
            @RequestBody @Valid List<PreSignedImageRequest> request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) throws IOException;


    @Operation(
            summary = "회원가입용 임시 파일 업로드용 URL API",
            description = "회원가입 용으로 임시 파일을 업로드할 수 있습니다."
    )
    ApiResponse<PreSignedImageResponse> tempUpload(
            @RequestBody @Valid PreSignedImageRequest request) throws IOException;

    @Operation(
            summary = "회원가입용 임시 파일 업로드용 저장 API",
            description = "S3에 올린 것을 확정합니다."
    )
    ApiResponse<ImageResponse> confirm(@RequestParam String key) throws IOException;


    @Operation(
            summary = "파일 업로드용 이미지 저장 API",
            description = "S3에 올린 것을 여러개의 파일을 확정합니다."
    )
    ApiResponse<List<ImageResponse>> confirm(
            @RequestParam List<String> keys,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) throws IOException;

}
