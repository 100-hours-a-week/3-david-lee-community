package bootcamp.kakao.community.platform.report.application;

import bootcamp.kakao.community.common.response.CustomException;
import bootcamp.kakao.community.common.response.ErrorCode;
import bootcamp.kakao.community.common.response.code.ReportErrorCode;
import bootcamp.kakao.community.platform.posts.comment.application.CommentUseCase;
import bootcamp.kakao.community.platform.posts.post.application.PostQueryUseCase;
import bootcamp.kakao.community.platform.report.application.dto.ReportRequest;
import bootcamp.kakao.community.platform.report.domain.entity.Report;
import bootcamp.kakao.community.platform.report.domain.repository.ReportRepository;
import bootcamp.kakao.community.platform.user.application.UserUseCase;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService implements ReportUseCase {

    private final ReportRepository reportRepository;

    /// 외부 의존성
    private final UserUseCase userService;
    private final CommentUseCase commentService;
    private final PostQueryUseCase postService;

    @Override
    public void createReport(ReportRequest request, Long userId) {

        /// 유저 예외처리
        User user = userService.loadUser(userId);

        /// 각 종류 contentId 예외 처리
        switch (request.type()){
            case COMMENT:
                /// 댓글인 경우
                commentService.loadComment(request.contentId());
                break;
            case POST:
                /// 게시글인 경우
                postService.loadPost(request.contentId());
                break;
            case USER:
                /// 유저인 경우
                userService.loadUser(request.contentId());
                break;
            default:
                throw new CustomException(ReportErrorCode.BAD_REQUEST_REPORT_TYPE);
        }

        /// 생성 및 저장
        var reqReport = Report.of(user, request.type(), request.contentId(), request.reason());
        reportRepository.save(reqReport);
    }
}
