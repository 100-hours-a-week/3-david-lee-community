## 🚀 소개
<img width="591" height="214" alt="image" src="https://github.com/user-attachments/assets/451444c0-54a5-4edc-b02d-6a6d9c020fd1" />

---

## 🧰 기술 스택

**백엔드**
* **Java 21**, **Spring Boot 3.56**
* Spring Web, Spring Validation, Spring Data JPA, Spring Security
* **JWT**(Access/Refresh) – 쿠키 발급
* **QueryDSL** – 동적 조회
* **MySQL** (InnoDB), **Redis**, **S3**
* Gradle, Docker

**인프라**
* **AWS EC2**, **ALB**, **API Gateway**
* **ElastiCache**, **RDS**
* **ECR**, **Code Deploy**
* **Grafana**, **Prometheus**

---

## 시연 영상
https://youtu.be/-OU1zNI22rg?si=DQVjqFTWri5dhAem

---

## 💡서비스 기능 및 이유 설명

### 🔐 인증/보안
* 요청 시 JWT 검증 필터가 토큰을 확인하여 인증
* 로그인 성공 시 Access Token을 헤더로 발급
* Refresh Token을 쿠키로 발급 (HttpOnly, Secure, SameSite 설정)
  * 토큰의 상태 관리를 위해서 Redis 구현

---

### 🖼️ 이미지 업로드 정책
* 클라이언트가 이미지 파일명을 보내면 서버가 Pre‑Signed URL 반환
* 클라이언트는 해당 URL로 S3에 직접 업로드
  *   서버의 부하를 방지하고자 프런트 측에서 호출

---
### 🔁 무한스크롤 규칙
* 리스트 API는 lastId, size 쿼리 파라미터를 사용
  *   기존 페이지네이션에서의 Offset 성능저하의 단점을 극복하고자, lastId기반의 QueryDSL 기반 쿼리문 작성 

---

### ⏰ 배치 처리 스케줄링

#### 1. 고아 이미지 자동 삭제
**처리 내용**:
- S3에 업로드되었지만 게시글에 사용되지 않은 이미지(confirmed=false) 자동 삭제
- Pre-Signed URL로 업로드 후 게시글 작성을 완료하지 않은 경우 발생하는 고아 이미지 정리
- S3 스토리지 비용 절감 및 불필요한 리소스 제거

#### 2. 게시글 통계 동기화 (PostBatchService)
**처리 내용**:
- Redis에 캐싱된 게시글 통계(조회수, 댓글수, 좋아요수)를 DB에 동기화
- 100개 단위 청크로 트랜잭션 처리하여 대용량 데이터 안정적 처리
- 동기화 완료 후 해당 Redis 키 자동 삭제

---

## 📁 백엔드 프로젝트 구조

```
project
├─ global/              # 공통 예외, 응답 래퍼, 설정
├─ security/            # JWT, 인증/인가 필터, 핸들러
├─ platform/
│  ├─ user/             # 유저 도메인 (회원가입/로그인/마이페이지)
│  ├─ posts/            # 게시글 도메인
│  ├─ comments/         # 댓글 도메인
│  └─ images/           # 이미지(Pre‑Signed URL 발급)
└─ ...
```
---

## 📊 백엔드 ERD (Entity Relationship Diagram)

```mermaid
erDiagram
    USERS ||--o{ POSTS : "작성"
    USERS ||--o{ COMMENTS : "작성"
    USERS ||--o{ POSTS_LIKES : "좋아요"
    USERS ||--o{ REPORT : "신고"
    USERS ||--o{ IMAGES : "업로드"

    CATEGORY ||--o{ POSTS : "분류"
    CATEGORY ||--o{ CATEGORY : "계층구조"

    POSTS ||--o{ COMMENTS : "포함"
    POSTS ||--o{ POSTS_LIKES : "받음"
    POSTS ||--o{ POSTS_IMAGES : "포함"

    COMMENTS ||--o{ COMMENTS : "대댓글"

    IMAGES ||--o{ POSTS_IMAGES : "매핑"

    USERS {
        bigint user_id PK
        varchar name
        varchar image_key
        varchar nickname UK
        varchar email UK "idx_user_email"
        varchar password
        enum role "MEMBER/ADMIN"
        boolean deleted
        timestamp created_at
        timestamp updated_at
    }

    POSTS {
        bigint post_id PK
        bigint user_id FK
        bigint category_id FK
        varchar thumbnail_key
        varchar title
        text content
        bigint view_count "PostStat 임베디드"
        bigint comment_count "PostStat 임베디드"
        bigint like_count "PostStat 임베디드"
        boolean deleted
        timestamp created_at
        timestamp updated_at
    }

    CATEGORY {
        bigint category_id PK
        bigint parent_id FK "nullable"
        varchar name UK
    }

    COMMENTS {
        bigint comment_id PK
        bigint post_id FK
        bigint user_id FK
        bigint parent_id FK "nullable, idx_comment_"
        varchar content
        boolean deleted
        timestamp created_at
        timestamp updated_at
    }

    POSTS_LIKES {
        bigint user_id PK,FK
        bigint post_id PK,FK
        timestamp created_at
        timestamp updated_at
    }

    REPORT {
        bigint report_id PK
        bigint user_id FK
        enum type "POST/COMMENT"
        bigint content_id
        varchar reason
        timestamp created_at
        timestamp updated_at
    }

    IMAGES {
        bigint id PK
        bigint user_id FK "nullable"
        varchar image_key UK "idx_image_key"
        boolean confirmed
        timestamp created_at
        timestamp updated_at
    }

    POSTS_IMAGES {
        bigint id PK
        bigint post_id FK
        bigint image_id FK
        int ord "순서"
        timestamp created_at
        timestamp updated_at
    }

    JWT_REFRESH_TOKEN {
        varchar id PK "Redis Key"
        bigint user_id "Indexed"
        varchar refresh_token "Indexed"
        varchar device_type "Indexed"
        long expire_time "TTL"
    }
```

#### 인덱스 전략
- `idx_user_email`: 로그인 시 이메일 기반 빠른 조회
- `idx_comment_`: 대댓글 조회 최적화
- `idx_image_key`: S3 키 기반 이미지 검색 최적화

--- 
## 🔨프로젝트 인프라 아키텍쳐
<img width="1080" height="1270" alt="아키텍쳐" src="https://github.com/user-attachments/assets/3366081d-6cdc-4bc1-b88b-34cb6b26a062" />
* AWS 3-tier 아키텍쳐 구현

이번 프로젝트는 비용적인 부분은 최소화시킨 채, 이용자 증가를 대비한 확장성 확보, 빠른 응답속도를 목표로 AWS에서 3-Tier 기반 아키텍처를 설계했습니다.

### 퍼블릭 서브넷
FE 
* 외부 유저가 직접 접근하는 웹 서비스이므로 퍼블릭 서브넷에 배치
* Route53 → ALB(SSL) → EC2 FE 서버
* SSL/TLS는 ALB에서 처리
* FE 이미지는 DockerHub에서 pull

BE 
* Route53 → API Gateway(SSL) → ALB(HTTP) → ASG
* 초기에 개발/테스트/비용 편의성을 위해 퍼블릭 서브넷에서 운영

* ⚠ 주의: BE가 퍼블릭에 있다고 해서 외부에서 바로 접근 가능한 것은 아니며,
  ALB Security Group만 허용하도록 제한하여 직접 접근을 방지했습니다.
즉, 인터넷에서 바로 API 서버를 때릴 수 있는 구조가 아님.

### 프라이빗 서브넷
* RDS / ElastiCache는 프라이빗 서브넷
* 외부 접근 차단, DB 보안 강화
* 단일 AZ 구성
* 애플리케이션 서버와만 통신

### ECR 구조
* 백엔드 이미지는 보안을 이유로 Private ECR 사용
* GitHub Actions에서 ECR로 push → CodeDeploy에서 pull

## 멀티 스테이징 도커파일 구성
```
# ====== Build Stage ======
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build
COPY . .
RUN ./gradlew bootJar

# ====== Runtime Stage ======
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/build/libs/community-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]

```
* Build Stage: JDK + Gradle로 JAR 빌드
* Runtime Stage: JRE만 포함 → 더 가벼움
* application-prod.yml은 이미지에 포함하지 않음
* 자주 사용하는 레이어는 고정시켜 빌드 시간 단축

## CI/CD 파이프라인 구조도
CI
* PR 단계: 단위 테스트만 수행 → 빠른 피드백 목표

CD
* develop merge 시: 단위 + 통합 테스트
* ECR push
* 앱 실행 스크립트 & docker-compose & appspec.zip → S3 업로드
* CodeDeploy가 ASG 대상 그룹에 Blue/Green 배포 자동 처리

## 블루/그린 배포
* CodeDeploy + ASG 활용
* Green 인스턴스 자동 생성 후 검증
* 정상 시 트래픽 100% 전환
* 문제 발생 시 자동 롤백
* 확장성과 안전성 확보

## 프로메테우스, 그라파나
<img width="1121" height="714" alt="image" src="https://github.com/user-attachments/assets/7269dce0-092c-4dcc-9b03-081eba415dda" />

* 본 프로젝트는 Auto Scaling 환경에서 서버 개수가 동적으로 변하기 때문에,
새로운 인스턴스가 생성·삭제되더라도 메트릭이 안정적으로 수집되는 구조를 목표로
Prometheus + Grafana 기반의 관찰성(Observability) 을 구성했습니다.

* EC2 Service Discovery 적용
ASG 환경에서는 인스턴스가 계속 생성/삭제되기 때문에
Prometheus가 AWS API를 통해 EC2 인스턴스를 자동 검색(SD) 하고
필요한 대상만 스크랩하도록 구성했습니다.
