package bootcamp.kakao.community.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ImageUtil {

    @Value("${cloud.aws.S3.bucket}")
    private static String bucketName;

    @Value("${cloud.aws.region.static}")
    private static String region;

    /// 파일 이름을 고유하게 생성하는 메서드
    public String generateKey(String file) {

        /// 랜덤 UUID
        String key = UUID.randomUUID().toString();

        /// 확장자 처리
        String extension = file.substring(file.lastIndexOf(".") + 1);

        return key + "." + extension;
    }

    /// key 바탕으로 퍼블릭 URL 접근하기
    public static String getUrlByKey(String key) {

        /// 응답
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }

}
