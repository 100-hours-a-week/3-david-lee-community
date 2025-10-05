package bootcamp.kakao.community.platform.posts.comment.domain.repository;

import bootcamp.kakao.community.common.response.paging.SliceRequest;
import bootcamp.kakao.community.platform.posts.comment.domain.entity.Comment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static bootcamp.kakao.community.platform.posts.comment.domain.entity.QComment.comment;
import static bootcamp.kakao.community.platform.posts.post.domain.entity.QPost.post;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Comment> findCommentsByCursor(SliceRequest sliceRequest, Long postId) {

        /// QueryDSL 조회
        /// 기존 댓글(루트 댓글) + 대댓글까지 한번에 가져오게 하려면?
        /// 1차로 루트 먼저 가져오게끔
        /// 2차로 해당 루트의 자식 댓글 가져오게끔

        List<Comment> fetch = queryFactory
                .selectFrom(comment)
                .where(
                        eqPostId(postId),
                        ltLastId(sliceRequest.lastId()),
                        comment.parent.isNull())    /// 루트 댓글만 조회
                .orderBy(post.id.desc())
                .limit(sliceRequest.offSet() + 1) // hasNext 확인용 +1
                .fetch();

        /// Slice 객체 생성
        boolean hasNext = false;
        if (fetch.size() > sliceRequest.offSet()) {

            /// 조회했던 값은 삭제
            fetch.remove(sliceRequest.offSet());
            hasNext = true;
        }

        /// Pageable 제작
        Pageable pageable = PageRequest.of(0, sliceRequest.offSet());

        /// 리턴
        return new SliceImpl<>(fetch, pageable, hasNext);
    }

    /// 커서 조건 (id 기반 커서)
    private BooleanBuilder ltLastId(Long lastId) {

        BooleanBuilder builder = new BooleanBuilder();

        if (lastId == null) {
            /// 해당 조건이 null 이면 패스한다.

            return builder;
        }

        return builder.and(comment.id.lt(lastId));
    }

    /// 게시글 ID Boolean 조건절
    BooleanBuilder eqPostId(Long postId) {

        BooleanBuilder builder = new BooleanBuilder();

        if (postId == null) {
            /// 해당 조건이 null 이면 패스한다.
            return builder;
        }

        return builder.and(comment.post.id.eq(postId));

    }
}
