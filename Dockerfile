# ====== Build Stage ======
FROM eclipse-temurin:21-jdk-alpine AS  builder
# 작업 디렉토리를 /build로 설정합니다.
WORKDIR /build
# 파일을 이동시킵니다.
COPY . .
# 실행 가능한 Jar를 만듭니다.
RUN ./gradlew bootJar

# ====== Runtime Stage ======
FROM eclipse-temurin:21-jre-alpine
# 실행 파일 이동
WORKDIR /app
# 빌드한 jar 파일 가져오기
COPY --from=builder /build/build/libs/community-0.0.1-SNAPSHOT.jar app.jar
# 노출할 포트
EXPOSE 8080
# 실행하기
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
