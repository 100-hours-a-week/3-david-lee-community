package bootcamp.kakao.community.platform.posts.category.presentation.swaager;

import bootcamp.kakao.community.common.response.ApiResponse;
import bootcamp.kakao.community.platform.posts.category.application.dto.CategoryRequest;
import bootcamp.kakao.community.platform.posts.category.application.dto.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import bootcamp.kakao.community.security.auth.annotation.CurrentUserId;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "카테고리 API", description = "카테고리 생성/조회/삭제하는 API 입니다.")
public interface CategoryApiSpec {

    /// 카테고리 작성
    @Operation(
            summary = "카테고리 생성 API",
            description = "카테고리를 생성하는 API 입니다."
    )
    ApiResponse<Void> create(@RequestBody @Valid CategoryRequest request,
                             @CurrentUserId Long userId);

    /// 카테고리 목록 조회
    @Operation(
            summary = "카테고리 목록 조회 API",
            description = "기본 카테고리를 조회하는 API 입니다."
    )
    ApiResponse<List<CategoryResponse>> list();


    /// 카테고리 삭제
    @Operation(
            summary = "카테고리 삭제 API",
            description = "카테고리를 삭제하는 API 입니다."
    )
    ApiResponse<Void> delete(
            @RequestParam Long categoryId,
            @CurrentUserId Long userId);

}
