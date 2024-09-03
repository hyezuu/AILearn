FROM openjdk:17-jdk-slim
ADD /build/libs/*.jar app.jar
COPY /build/libs/*.jar app.jar

# 환경변수 설정
ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]