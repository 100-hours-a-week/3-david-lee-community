package bootcamp.kakao.community.platform.batch;

import bootcamp.kakao.community.platform.images.image.domain.entity.Image;
import bootcamp.kakao.community.platform.images.image.domain.repository.ImageRepository;
import bootcamp.kakao.community.platform.images.image.external.ImageCloudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * 사용하지않는 이미지는 매일마다 삭제하도록
 */
@Component
@RequiredArgsConstructor
public class ImageBatchService {

    /// 삭제를 위한 클라우드 서비스
    private final ImageCloudUseCase cloudService;
    private final ImageRepository repository;

    /// 3시마다 삭제 프로세싱
    @Transactional
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteBatchEveryDay() throws IOException {

        /// 사용하지않는 이미지 목록 조회
        List<Image> confirmedFalse = repository.findByConfirmedFalse();

        /// 이미지 목록 추출하여 삭제
        List<String> imageUrls = confirmedFalse.stream()
                .map(Image::getKey)
                .toList();

        /// 한번에 삭제 처리
        cloudService.deleteFile(imageUrls);
    }

}
