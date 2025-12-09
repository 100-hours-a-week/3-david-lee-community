package bootcamp.kakao.community.common.response.paging;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.List;

@Builder
@Schema(name = "[응답][공통] 페이징 응답 객체")
public record SliceResponse<T>(
        List<T> content,
        boolean hasNext
) {
    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return SliceResponse.<T>builder()
                .content(slice.getContent())
                .hasNext(slice.hasNext())
                .build();
    }
}
