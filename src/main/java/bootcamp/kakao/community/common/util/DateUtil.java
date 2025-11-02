package bootcamp.kakao.community.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {

    public static String formatPostDate(LocalDateTime created) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(created, now);

        if (duration.toMinutes() < 60) {
            return duration.toMinutes() + "분 전";
        } else if (duration.toHours() < 24) {
            return duration.toHours() + "시간 전";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MMM dd일 HH시 mm분", Locale.KOREAN);
            return created.format(formatter);
        }
    }
}
