package bootcamp.kakao.community.common.logging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LogType {

    HTTP("[HTTP 로그]"),
    ERROR_401("[인증 에러]"),
    ERROR_403("[인가 에러]");

    private final String label;

}
