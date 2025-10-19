package bootcamp.kakao.community.platform.posts.post.application;

import bootcamp.kakao.community.platform.posts.post.application.dto.PostRequest;
import bootcamp.kakao.community.platform.posts.post.application.dto.PostSaveResponse;
import bootcamp.kakao.community.platform.posts.post.application.dto.PostUpdateRequest;

public interface PostCommandUseCase {

    /// 글 작성
    PostSaveResponse create(PostRequest req, Long userId);

    /// 게시글 수정하기
    void update(PostUpdateRequest req, Long userId);

    /// 게시글 삭제하기
    void delete(Long postId, Long userId);

}
