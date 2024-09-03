FROM openjdk:17-jdk-slim
ADD /build/libs/*.jar app.jar
COPY /build/libs/*.jar app.jar

# 환경변수 설정
ENV SPRING_PROFILES_ACTIVE=dev

# 타임존 설정
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
    && echo "Asia/Seoul" > /etc/timezone

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]