package bootcamp.kakao.community.platform.posts.category.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @param parentId  상위 카테고리
 * @param name      카테고리 명
 */
@Schema(name = "[요청][카테고리] 카테고리 등록 Request", description = "카테고리 등록을 위한 Request DTO입니다.")
public record CategoryRequest(
        @Schema(description = "상위 카테고리 ID", example = "null")
        Long parentId,

        @Schema(description = "카테고리 명", example = "예시")
        String name
) {
}
