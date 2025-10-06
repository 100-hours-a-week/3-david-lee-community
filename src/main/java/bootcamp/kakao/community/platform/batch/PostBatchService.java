package bootcamp.kakao.community.platform.batch;

import bootcamp.kakao.community.common.util.KeyUtil;
import bootcamp.kakao.community.platform.posts.post.domain.entity.Post;
import bootcamp.kakao.community.platform.posts.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostBatchService {

    private final PostRepository repository;
    private final StringRedisTemplate redisTemplate;
    private static final int CHUNK_SIZE = 100;

    /**
     * 매일 오전 4시에 Redis의 게시글 통계 정보를 DB에 업데이트하고, 처리된 Redis 키를 삭제합니다.
     * CHUNK_SIZE 만큼 트랜잭션을 유지하고자 합니다.
     */
    @Transactional
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void syncPostStatsToDB() {

        log.info("[배치 로깅] 게시글 통계 DB 동기화 및 Redis 키 삭제 배치 작업을 시작합니다.");

        Page<Post> currentPage = null;
        Pageable pageable = PageRequest.of(0, CHUNK_SIZE);

        do {
            try {
                /// Chunk 단위로 데이터를 조회하고 DB에 업데이트합니다.
                currentPage = updatePostStatsInChunk(pageable);

                /// DB 업데이트가 성공한 Chunk의 Redis 키들을 삭제합니다.
                if (currentPage != null && !currentPage.getContent().isEmpty()) {
                    List<Post> processedPosts = currentPage.getContent();
                    List<String> keysToDelete = new ArrayList<>();

                    /// 삭제할 키를 리스트에 추가
                    processedPosts.forEach(post -> {
                        keysToDelete.add(KeyUtil.getPostView(post.getId()));
                        keysToDelete.add(KeyUtil.getPostComment(post.getId()));
                        keysToDelete.add(KeyUtil.getPostLike(post.getId()));
                    });

                    /// 한번에 키 삭제
                    redisTemplate.delete(keysToDelete);
                    log.info("[배치 로깅] Page {}의 Redis 키 {}개를 성공적으로 삭제했습니다.", pageable.getPageNumber(), keysToDelete.size());
                }

                /// 다음 페이지로 이동
                pageable = currentPage.nextPageable();

            } catch (Exception e) {
                log.error("[배치 로깅] 게시글 통계 동기화 중 오류 발생. page: {}", pageable.getPageNumber(), e);

                /// 오류 발생 시 해당 Chunk는 건너뛰고 다음으로 진행
                if (pageable.getPageNumber() > 0) {
                    pageable = pageable.next();
                } else {
                    break; // 첫 페이지에서 에러나면 중단
                }
            }
        } while (currentPage != null && !currentPage.isLast());

        log.info("[배치 로깅] 게시글 통계 DB 동기화 및 Redis 키 삭제 배치 작업을 성공적으로 완료했습니다.");
    }

    /**
     * 한 페이지(Chunk) 단위로 게시글 통계를 처리합니다.
     */
    @Transactional
    public Page<Post> updatePostStatsInChunk(Pageable pageable) {

        /// 페이징에 해당하는 것 다 가져오기
        Page<Post> postPage = repository.findAll(pageable);
        List<Post> postList = postPage.getContent();

        if (postList.isEmpty()) {
            return postPage;
        }

        /// 조회수
        List<String> viewCountKeys = postList.stream()
                .map(p -> KeyUtil.getPostView(p.getId()))
                .toList();

        /// 댓글수
        List<String> commentCountKeys = postList.stream()
                .map(p -> KeyUtil.getPostComment(p.getId()))
                .toList();

        /// 좋아요수
        List<String> likeCountKeys = postList.stream()
                .map(p -> KeyUtil.getPostLike(p.getId()))
                .toList();

        /// 순서대로 가져오기
        List<String> viewCounts = redisTemplate.opsForValue().multiGet(viewCountKeys);
        List<String> commentCounts = redisTemplate.opsForValue().multiGet(commentCountKeys);
        List<String> likeCounts = redisTemplate.opsForValue().multiGet(likeCountKeys);

        /// 순서대로 매핑해서 저장하기
        for (int i = 0; i < postList.size(); i++) {
            Post post = postList.get(i);

            String redisViewCount = viewCounts != null ? viewCounts.get(i) : null;
            String redisCommentCount = commentCounts != null ? commentCounts.get(i) : null;
            String redisLikeCount = likeCounts != null ? likeCounts.get(i) : null;

            /// 수정할 값이 전부 없으면 안한다.
            if (redisViewCount != null || redisCommentCount != null || redisLikeCount != null) {

                /// 있으면 Redis, 없으면 DB 값으로 수정
                long viewCount = (redisViewCount != null) ? Long.parseLong(redisViewCount) : post.getPostStat().getViewCount();
                long commentCount = (redisCommentCount != null) ? Long.parseLong(redisCommentCount) : post.getPostStat().getCommentCount();
                long likeCount = (redisLikeCount != null) ? Long.parseLong(redisLikeCount) : post.getPostStat().getLikeCount();

                /// 전체 값 수정하고 저장하기
                post.getPostStat().updateStat(commentCount, likeCount, viewCount);
                repository.save(post);
            }
        }
        return postPage;
    }
}

