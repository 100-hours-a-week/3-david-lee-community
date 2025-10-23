package bootcamp.kakao.community.platform.images.post_images.application;

import bootcamp.kakao.community.platform.images.image.application.ImageUseCase;
import bootcamp.kakao.community.platform.images.image.domain.entity.Image;
import bootcamp.kakao.community.platform.images.post_images.domain.entity.PostImage;
import bootcamp.kakao.community.platform.images.post_images.domain.repository.PostImageRepository;
import bootcamp.kakao.community.platform.posts.post.domain.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PostImageService implements PostImageUseCase {

    private final PostImageRepository repository;
    private final ImageUseCase imageService;


    /// 게시글에 이미지 저장
    @Override
    @Transactional
    public PostImage savePostImage(Post post, Image image) {

        /// 게시글 이미지 저장하기
        PostImage postImage = PostImage.of(post, image, 0);

        /// 저장하기
        return repository.save(postImage);
    }

    /// 게시글에 여러개 이미지 저장
    /// PostImage의 of를 통해 이미지 사용이 확정된다.
    @Override
    @Transactional
    public List<PostImage> savePostImage(Post post, List<String> imageUrls) {

        /// 요청한 실제 이미지가 있는지, 불러오기 (영속성 컨테이너)
        List<Image> images = imageService.getImage(imageUrls);

        /// List 형식 극복하기
        List<PostImage> postImages = IntStream.range(0, images.size())
                .mapToObj(i -> PostImage.of(post, images.get(i), i))
                .toList();

        /// 저장하기
        return repository.saveAll(postImages);

    }

    /// 게시글의 여러개 이미지 조회
    @Override
    @Transactional(readOnly = true)
    public List<PostImage> loadPostImages(Post post) {
        return repository.findByPost(post);
    }

    /// 게시글 이미지 수정하기 (삭제 후, 다시 추가)
    @Override
    @Transactional
    public String updatePostImages(Post post, List<String> imageUrls) {

        /// 기존에 있던 값 조회
        List<PostImage> oldPostImages = repository.findByPost(post);

        /// 새롭게 요청한 실제 이미지가 있는지, 불러오기 (영속성 컨테이너)
        List<Image> newImages = imageService.getImage(imageUrls);

        /// 새로 추가한 값에서, 기존에 사용하지 않는 사진은 삭제
        // 요청 이미지들의 Id 변환
        Set<Long> newIds = newImages.stream()
                .map(Image::getId)
                .collect(Collectors.toSet());

        // 기존에 있던 값에서 사용하지 않는 이미지는 삭제 상태처리
        List<PostImage> notUseImages = oldPostImages.stream()
                .filter(oldIm -> !newIds.contains(oldIm.getId()))
                .toList();

        // Image 삭제 상태로 만들기
        for (PostImage image : notUseImages) {
            image.getImage().unConfirm();
        }

        /// 순서대로 다시 추가하기
        // 기존 PostImage 전부 삭제하기, S3는 삭제되지않으므로 괜찮을듯.
        repository.deleteAll(oldPostImages);

        /// List 형식 극복하기
        List<PostImage> postImages = IntStream.range(0, newImages.size())
                .mapToObj(i -> PostImage.of(post, newImages.get(i), i))
                .toList();

        if (!postImages.isEmpty()) {
            /// 존재한다면 저장 후,첫 이미지 URL 반환
            repository.saveAll(postImages);
            return postImages.get(0).getImage().getKey();
        }

        /// 없으면
        return null;
    }
}
