# 빌드 단계
FROM openjdk:21-slim AS builder

WORKDIR /app

# 모든 파일을 복사
COPY . .

# gradlew 실행 파일 복사 및 실행 권한 부여
COPY gradlew /app/gradlew
RUN chmod +x /app/gradlew

# Gradle 빌드 실행
RUN ./gradlew build

# 실행 단계
FROM openjdk:21-slim

WORKDIR /app

# redis-tools 설치를 위한 apt-get 사용
RUN apt-get update && apt-get install -y redis-tools && apt-get clean

COPY --from=builder /app/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
