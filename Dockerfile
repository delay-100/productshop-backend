# 빌드 단계
FROM openjdk:21-slim AS builder
# 안되면 amazoncorretto:21-jdk로 바꿔야대나 ㄷㄷ
# alpine을 사용하면 일반 이미지에 비해 가볍기 떄문에 빌드, 배포 속도가 빨라짐
# eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY .. .

# 작업 디렉토리 복사 (RUN 명령어 최소화)
COPY gradlew.bat .
# 혹은
# COPY gradlew /app/gradlew  # gradlew 실행 파일 복사 (Linux/macOS 환경 가정)

# 실행 단계
FROM openjdk:21-slim

WORKDIR /app

# redis-tools 설치를 위한 apk 사용
RUN apt-get update && apt-get install -y redis-tools && apt-get clean

COPY --from=builder /app/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]