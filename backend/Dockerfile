# ===== 1단계: 빌드 스테이지 =====
FROM gradle:8.14.5-jdk17 AS builder
WORKDIR /app

# 의존성 캐시 최적화: 빌드 스크립트를 먼저 복사해 의존성만 미리 받는다
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

# 나머지 소스를 복사하고 실행 가능한 jar를 빌드 (테스트는 CI에서 수행)
COPY src ./src
RUN gradle bootJar --no-daemon -x test

# ===== 2단계: 실행 스테이지 =====
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 보안: root가 아닌 일반 사용자로 실행
RUN useradd -r -u 1001 appuser
USER appuser

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/app.jar"]
