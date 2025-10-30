package bootcamp.kakao.community.security.auth.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthHistoryType {

    FAIL("로그인 실패"),
    SUCCESS("로그인 성공");

    private final String label;


    @JsonValue
    public String getLabel() {
        return label;
    }
}
