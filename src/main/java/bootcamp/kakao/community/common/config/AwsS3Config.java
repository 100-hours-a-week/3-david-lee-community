package bootcamp.kakao.community.common.config;

import bootcamp.kakao.community.common.response.code.ImageErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
public class AwsS3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String accessSecret;

    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * Pre signed URL을 만들기 위한 S3Presigner
     */
    @Bean
    public S3Presigner s3Presigner() {
        if (accessKey == null || accessSecret == null || region == null) {
            throw new IllegalStateException(ImageErrorCode.INTERNAL_S3_ERROR.getMessage());
        }

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, accessSecret);
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    /**
     * 파일 삭제 등 객체 관리를 위한 S3Client 빈 생성
     */
    @Bean
    public S3Client s3Client() {
        if (accessKey == null || accessSecret == null || region == null) {
            throw new IllegalStateException(ImageErrorCode.INTERNAL_S3_ERROR.getMessage());
        }

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, accessSecret);
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

}
