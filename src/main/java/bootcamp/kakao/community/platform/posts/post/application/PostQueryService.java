package bootcamp.kakao.community.platform.posts.post.application;

import bootcamp.kakao.community.common.response.paging.SliceRequest;
import bootcamp.kakao.community.common.response.paging.SliceResponse;
import bootcamp.kakao.community.common.util.KeyUtil;
import bootcamp.kakao.community.platform.images.post_images.application.PostImageUseCase;
import bootcamp.kakao.community.platform.images.post_images.domain.entity.PostImage;
import bootcamp.kakao.community.platform.posts.category.application.CategoryUseCase;
import bootcamp.kakao.community.platform.posts.category.domain.entity.Category;
import bootcamp.kakao.community.platform.posts.post.application.dto.PostDetailResponse;
import bootcamp.kakao.community.platform.posts.post.application.dto.PostListResponse;
import bootcamp.kakao.community.platform.posts.post.domain.entity.Post;
import bootcamp.kakao.community.platform.posts.post.domain.repository.PostRepository;
import bootcamp.kakao.community.platform.posts.post_likes.application.PostLikeUseCase;
import bootcamp.kakao.community.platform.user.application.UserUseCase;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostQueryService implements PostQueryUseCase{

    private final PostRepository repository;

    /// 레디스 정의
    private final StringRedisTemplate redisTemplate;

    /// 외부 의존성
    private final UserUseCase userService;
    private final CategoryUseCase categoryService;
    private final PostImageUseCase postImageService;
    private final PostLikeUseCase likeUseCase;

    /// 게시글 목록 조회
    @Override
    @Transactional(readOnly = true)
    public SliceResponse<PostListResponse> getPosts(SliceRequest req, Long categoryId) {

        /// 카테고리 예외처리
        Category category = loadCategory(categoryId);

        /// DB에서 게시글 목록 조회
        Slice<Post> posts = repository.findPostsByCursor(req, category.getName(), false);
        List<Post> postList = posts.getContent();

        /// Redis에서 한번에 조회할 키 목록 생성
        List<String> viewCountKeys = postList.stream()
                .map(p -> KeyUtil.getPostView(p.getId()))
                .toList();

        List<String> commentCountKeys = postList.stream()
                .map(p -> KeyUtil.getPostComment(p.getId()))
                .toList();

        List<String> likeCountKeys = postList.stream()
                .map(p -> KeyUtil.getPostLike(p.getId()))
                .toList();

        /// Redis에 multiGet 요청으로 데이터 한번에 가져오기
        List<String> viewCounts = redisTemplate.opsForValue().multiGet(viewCountKeys);
        List<String> commentCounts = redisTemplate.opsForValue().multiGet(commentCountKeys);
        List<String> likeCounts = redisTemplate.opsForValue().multiGet(likeCountKeys);

        /// Post 목록을 순회하며 PostListResponse 생성
        List<PostListResponse> responses = new ArrayList<>();

        for (int i = 0; i < postList.size(); i++) {
            Post post = postList.get(i);

            /// Redis에서 가져온 값
            String redisViewCount = viewCounts.get(i);
            String redisCommentCount = commentCounts.get(i);
            String redisLikeCount = likeCounts.get(i);

            /// Redis에 값이 없으면 DB의 PostStat 값 사용
            Long finalViewCount = (redisViewCount != null) ? Long.parseLong(redisViewCount) : post.getPostStat().getViewCount();
            Long finalCommentCount = (redisCommentCount != null) ? Long.parseLong(redisCommentCount) : post.getPostStat().getCommentCount();
            Long finalLikeCount = (redisLikeCount != null) ? Long.parseLong(redisLikeCount) : post.getPostStat().getLikeCount();

            /// 추가
            responses.add(
                    PostListResponse.from(post, finalViewCount, finalCommentCount, finalLikeCount)
            );
        }

        /// 리턴
        Slice<PostListResponse> responseSlice = new SliceImpl<>(responses, posts.getPageable(), posts.hasNext());
        return SliceResponse.from(responseSlice);
    }

    /// 인기 게시글 목록 조회
    @Override
    @Transactional(readOnly = true)
    public SliceResponse<PostListResponse> getFavoritePosts(SliceRequest req) {
        return null;
    }

    /// 게시글 상세 조회
    /// 조회수가 상승해야한다.
    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long postId, Long userId) {

        /// 게시글 DB 조회
        Post post = loadPost(postId);

        /// 게시글 이미지 조회하기
        List<PostImage> images = postImageService.loadPostImages(post);

        /// 조회수, 댓글수,좋아요수 가져오기
        Long viewCount = getViewCount(post);
        Long commentCount = getCommentCount(post);
        Long likeCount = getLikeCount(post);

        /// 비회원이 조회했다면
        if (userId == null) {
            /// 게시글의 정보만 전달하면 된다.
            return PostDetailResponse.from(post, images, viewCount, commentCount, likeCount);
        } else {
            /// 회원이 조회했다면,
            User user = loadUser(userId);

            /// 좋아요 여부 조회
            boolean liked = likeUseCase.isLiked(postId, userId);

            /// 편집 가능 여부 조회
            /// ID는 프록시 객체이기에 getUser의 Id를 해도 지연로딩이 발생하지않음!, 성능만 문제 X
            boolean editable = post.getUser().getId().equals(user.getId());

            /// 응답
            return PostDetailResponse.from(post, images, viewCount, commentCount, likeCount, liked, editable);
        }
    }


    // =================
    //  외부 로직
    // =================
    @Transactional
    public Post loadPost(Long postId) {
        return repository.findByIdAndDeletedIsFalse(postId)
                .orElseThrow(() -> new NoSuchElementException("해당 아이디가 존재하는 게시글이 없습니다."));
    }

    // =================
    //  내부 로직
    // =================

    private User loadUser(Long userId) {
        return userService.loadUser(userId);
    }

    private Category loadCategory(Long categoryId) {
        return categoryService.loadCategory(categoryId);
    }

    /// 조회 수 가져오기
    private Long getViewCount(Post post) {

        /// 레디스 키 조회
        String postViewKey = KeyUtil.getPostView(post.getId());
        Long value;

        try {
            /// Redis 값 조회
            String redisValue = redisTemplate.opsForValue().get(postViewKey);

            if (redisValue != null) {
                /// Redis 값 존재 -> +1 증가
                value = Long.parseLong(redisValue) + 1L;
            } else {
                /// Redis 값 없음 -> DB 값 기반 초기화
                value = post.getPostStat().getViewCount() + 1L;
            }

            /// Redis 업데이트
            redisTemplate.opsForValue().set(postViewKey, String.valueOf(value));

        } catch (Exception e) {
            /// 예외 발생 시 DB 조회 및 저장
            value = post.getPostStat().getViewCount() + 1L;

            /// 저장
            post.getPostStat().updateViewCount(value);
            repository.save(post);
        }

        return value;
    }

    /// 댓글 수 가져오기
    private Long getCommentCount(Post post) {

        /// 레디스 키 조회
        String postCommentKey = KeyUtil.getPostComment(post.getId());
        Long value;

        try {
            /// Redis 값 조회
            String redisValue = redisTemplate.opsForValue().get(postCommentKey);

            if (redisValue != null) {
                /// Redis 값 존재 -> +1 증가
                value = Long.parseLong(redisValue);
            } else {
                /// Redis 값 없음 -> DB 값 기반 초기화
                value = post.getPostStat().getCommentCount();
            }

            /// Redis 업데이트
            log.info("레디스 업데이트");
            redisTemplate.opsForValue().set(postCommentKey, String.valueOf(value));

        } catch (Exception e) {
            /// 예외 발생 시 DB 조회
            log.info("DB 업데이트");
            value = post.getPostStat().getCommentCount();
        }

        return value;


    }

    /// 좋아요 수 가져오기
    private Long getLikeCount(Post post) {

        /// 레디스 키 조회
        String postLikeKey = KeyUtil.getPostLike(post.getId());
        Long value;

        try {
            /// Redis 값 조회
            String redisValue = redisTemplate.opsForValue().get(postLikeKey);

            if (redisValue != null) {
                /// Redis 값 존재 -> +1 증가
                value = Long.parseLong(redisValue);
            } else {
                /// Redis 값 없음 -> DB 값 기반 초기화
                value = post.getPostStat().getLikeCount();
            }

            /// Redis 업데이트
            redisTemplate.opsForValue().set(postLikeKey, String.valueOf(value));

        } catch (Exception e) {
            /// 예외 발생 시 DB 조회
            value = post.getPostStat().getLikeCount();
        }

        return value;


    }

}
