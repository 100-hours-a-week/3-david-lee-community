package bootcamp.kakao.community.platform.posts.comment.application;

import bootcamp.kakao.community.common.response.paging.SliceRequest;
import bootcamp.kakao.community.common.response.paging.SliceResponse;
import bootcamp.kakao.community.common.util.KeyUtil;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentListResponse;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentRequest;
import bootcamp.kakao.community.platform.posts.comment.application.dto.CommentUpdateRequest;
import bootcamp.kakao.community.platform.posts.comment.domain.entity.Comment;
import bootcamp.kakao.community.platform.posts.comment.domain.repository.CommentRepository;
import bootcamp.kakao.community.platform.posts.comment.domain.repository.dto.CommentWithChildren;
import bootcamp.kakao.community.platform.posts.post.application.PostQueryUseCase;
import bootcamp.kakao.community.platform.posts.post.domain.entity.Post;
import bootcamp.kakao.community.platform.user.application.UserUseCase;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentUseCase{

    private final CommentRepository repository;
    private final PostQueryUseCase postService;
    private final UserUseCase userService;

    /// 레디스 정의
    private final StringRedisTemplate redisTemplate;

    /// 댓글 생성
    @Override
    @Transactional
    public void createComment(CommentRequest request, Long userId) {

        /// 유저 예외처리
        User user = userService.loadUser(userId);

        /// 게시글 예외처리
        Post post = postService.loadPost(request.postId());

        Comment parent = null;
        if (request.parentId() != null){
            /// 부모 댓글이 있다면 조회
            parent = loadComment(request.parentId());
        }

        /// 객체 생성 및 저장
        var reqComment = Comment.of(post, user, parent, request.content());
        repository.save(reqComment);

        /// 레디스에 값 1개 추가
        String postViewKey = KeyUtil.getPostComment(post.getId());
        redisTemplate.opsForValue().increment(postViewKey, 1L);
    }

    /**
     * 게시글에 따른 댓글 목록 조회
     * @param request   Slice 요청 DTO
     * @param postId    조회할 게시글
     * @param userId    조회하는 유저 (편집 가능 여부 판단)
     */
    @Override
    @Transactional(readOnly = true)
    public SliceResponse<CommentListResponse> getComments(SliceRequest request, Long postId, Long userId) {

        /// 게시글 예외처리
        Post post = postService.loadPost(postId);

        /// 가져오기 (부모 댓글과 대댓글 존재 )
        Slice<CommentWithChildren> comments = repository.findCommentsByCursor(request, post.getId());

        /// 리턴
        Slice<CommentListResponse> var = CommentListResponse.from(comments, userId);
        return SliceResponse.from(var);
    }

    /// 인기 게시글 조회하기
    @Override
    @Transactional(readOnly = true)
    public SliceResponse<CommentListResponse> getFavoriteComments(SliceRequest request, Long postId) {

        /// 게시글 예외처리
        Post post = postService.loadPost(postId);

        /// 가져오기
        // TODO 인기 댓글 조회하기

        /// 리턴
        return null;
    }

    @Override
    @Transactional
    public void updateComment(CommentUpdateRequest request, Long userId) {

        /// 유저 예외처리
        User user = userService.loadUser(userId);

        /// 나의 댓글인 지 조회
        /// ID 존재 및 작성 여부를 한번에 파악
        Comment comment = repository.findByUserAndId(user, request.id())
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 수정할 댓글이 없습니다."));

        /// 더티체킹 수정
        comment.update(request.content());

    }


    /// 삭제하기 (더티체킹)
    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {

        /// 유저 예외처리
        User user = userService.loadUser(userId);

        /// ID 존재 및 작성 여부를 한번에 파악 (영속성 컨테이너)
        Comment comment = repository.findByUserAndId(user, commentId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 삭제할 댓글이 없습니다."));

        /// 삭제
        comment.delete();

        /// 레디스에 값 1개 삭제
        String postViewKey = KeyUtil.getPostComment(comment.getPost().getId());
        redisTemplate.opsForValue().decrement(postViewKey, 1L);

    }

    // =================
    //  외부 사용 로직
    // =================
    /// 외부 댓글 조회
    @Override
    @Transactional
    public Comment loadComment(Long commentId) {
        return repository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("해당 아이디를 가진 댓글은 존재하지않습니다."));
    }
}

