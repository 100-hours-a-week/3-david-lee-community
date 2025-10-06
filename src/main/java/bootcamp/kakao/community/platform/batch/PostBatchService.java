package bootcamp.kakao.community.platform.batch;

import bootcamp.kakao.community.common.util.KeyUtil;
import bootcamp.kakao.community.platform.posts.post.domain.entity.Post;
import bootcamp.kakao.community.platform.posts.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 게시글의 조회수,댓글수, 좋아요수는 매일마다 DB에 백업 매핑해두록
 */
@Component
@RequiredArgsConstructor
public class PostBatchService {

    /// 값 매핑
    private final PostRepository repository;

    /// 레디스
    private final StringRedisTemplate redisTemplate;

    /// 레디스에 조회한 내용을 DB에 연동하기
    @Transactional
    @Scheduled(cron = "0 0 6 * * *")
    public void updateBatchEveryDays() {

        /// DB에서 모든 게시글 목록 조회 (영속성 컨테이너)
        List<Post> postList = repository.findAll();

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

        /// 매번 반복
        for (int i = 0; i < postList.size(); i++) {
            Post post = postList.get(i);

            // Redis에서 가져온 값
            String redisViewCount = viewCounts.get(i);
            String redisCommentCount = commentCounts.get(i);
            String redisLikeCount = likeCounts.get(i);

            // Redis에 값이 없으면 DB의 PostStat 값 사용
            Long finalViewCount = (redisViewCount != null) ? Long.parseLong(redisViewCount) : post.getPostStat().getViewCount();
            Long finalCommentCount = (redisCommentCount != null) ? Long.parseLong(redisCommentCount) : post.getPostStat().getCommentCount();
            Long finalLikeCount = (redisLikeCount != null) ? Long.parseLong(redisLikeCount) : post.getPostStat().getLikeCount();

            /// 추가
            post.getPostStat().updateStat(finalCommentCount, finalLikeCount, finalViewCount);
        }

    }

}
