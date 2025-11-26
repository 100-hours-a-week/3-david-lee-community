# Java 21기반의 공식 이미지를 사용합니다.
FROM eclipse-temurin:21-jdk

# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 기존 JAR 파일을 이동시킵니다.
COPY build/libs/*.jar app.jar

# 애플리케이션이 사용할 포트를 노출합니다.
EXPOSE 8080

# 컨테이너가 실행될 때 앱을 시작합니다.
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
