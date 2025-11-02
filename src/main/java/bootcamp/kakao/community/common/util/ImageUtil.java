package bootcamp.kakao.community.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ImageUtil {

    private static String bucketName;
    private static String region;

    @Value("${cloud.aws.S3.bucket}")
    public void setBucketName(String bucket) {
        ImageUtil.bucketName = bucket;
    }

    @Value("${cloud.aws.region.static}")
    public void setRegion(String region) {
        ImageUtil.region = region;
    }

    /// Key 생성하는 함수
    public static String generateKey(String fileName) {
        String key = UUID.randomUUID().toString();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        return key + "." + extension;
    }

    /// key를 URL로 만드는 함수
    public static String getUrlByKey(String key) {

        /// null이면 없이 응답
        if (key == null) {
            return null;
        }

        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }
}

