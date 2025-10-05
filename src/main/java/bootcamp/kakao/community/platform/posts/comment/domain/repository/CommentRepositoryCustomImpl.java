package bootcamp.kakao.community.platform.posts.comment.domain.repository;

import bootcamp.kakao.community.common.response.paging.SliceRequest;
import bootcamp.kakao.community.platform.posts.comment.domain.entity.Comment;
import bootcamp.kakao.community.platform.posts.comment.domain.entity.QComment;
import bootcamp.kakao.community.platform.posts.comment.domain.repository.dto.CommentWithChildren;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static bootcamp.kakao.community.platform.posts.comment.domain.entity.QComment.comment;
@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /// 고민해보다가, 매핑하는 부분에서GPT의 도움을 좀 받았습니다 ㅠㅠ
    @Override
    public Slice<CommentWithChildren> findCommentsByCursor(SliceRequest sliceRequest, Long postId) {

        /// QueryDSL 조회
        /// 기존 댓글(루트 댓글) + 대댓글까지 한번에 가져오게 하려면?
        /// 1차로 루트 먼저 가져오게끔
        /// 2차로 해당 루트의 자식 댓글 가져오게끔

        // 루트 댓글과 자식 댓글 존재 여부를 left join 해 존재하는지 판단
        QComment child = new QComment("child");

        /// 1차
        List<Comment> rootComments = queryFactory
                .selectFrom(comment)
                .leftJoin(child).on(child.parent.id.eq(comment.id), child.deleted.isFalse())
                .where(
                        eqPostId(postId),
                        ltLastId(sliceRequest.lastId()),
                        comment.parent.isNull(),
                        comment.deleted.isFalse()
                                .or(comment.deleted.isTrue().and(child.id.isNotNull()))
                )
                .groupBy(comment.id)
                .orderBy(comment.id.desc())
                .limit(sliceRequest.offSet() + 1)
                .fetch();

        /// 없으면 그대로 리턴
        if (rootComments.isEmpty()) {
            return new SliceImpl<>(List.of(), PageRequest.of(0, sliceRequest.offSet()), false);
        }

        // 루트 순서 보존용(LinkedHashMap) + id 목록
        List<Long> rootIds = rootComments.stream().map(Comment::getId).toList();

        /// 2차
        List<Comment> childComments = queryFactory
                .selectFrom(comment)
                .where(
                        comment.parent.id.in(rootIds),   /// 루트 댓글이 있는 것만 가져오도록
                        eqPostId(postId),        /// 게시글 ID 가져오기
                        comment.deleted.isFalse()
                )
                .orderBy(comment.parent.id.asc(), comment.id.asc())
                .fetch();

        /// 매핑 시키기 (GPT 도움 ..)
        // parentId를 바탕으로 children 리스트로 그룹핑
        Map<Long, List<Comment>> childrenByParentId = childComments.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        c -> c.getParent().getId(),
                        java.util.LinkedHashMap::new,
                        java.util.stream.Collectors.toList()
                ));

        /// List 매핑
        List<CommentWithChildren> mappedComments = rootComments.stream()
                .map(root -> CommentWithChildren.from(
                        root,
                        childrenByParentId.getOrDefault(root.getId(), List.of())
                ))
                .toList();
        /// 도움 끝

        /// Slice 객체 생성
        boolean hasNext = false;
        if (rootComments.size() > sliceRequest.offSet()) {

            /// 조회했던 값은 삭제
            rootComments.remove(sliceRequest.offSet());
            hasNext = true;
        }

        /// Pageable 제작
        Pageable pageable = PageRequest.of(0, sliceRequest.offSet());

        /// 리턴
        return new SliceImpl<>(mappedComments, pageable, hasNext);

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
