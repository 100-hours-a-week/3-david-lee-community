package bootcamp.kakao.community.security.auth.domain;

import bootcamp.kakao.community.platform.BaseTimeEntity;
import bootcamp.kakao.community.platform.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String ipAddress;

    private String device;

    @Enumerated(value = EnumType.STRING)
    private AuthHistoryType status;
    /// 빌더 생성자
    @Builder
    protected AuthHistory(User user, String ipAddress, String device, AuthHistoryType status) {
        this.user = user;
        this.ipAddress = ipAddress;
        this.device = device;
        this.status = status;
    }

    /// 정적 팩토리 메서드
    public static AuthHistory of(User user, String ipAddress, String device, AuthHistoryType status) {
        return AuthHistory.builder()
                .user(user)
                .ipAddress(ipAddress)
                .device(device)
                .status(status)
                .build();
    }

}
