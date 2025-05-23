# Docker 镜像构建
# @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>
# @from <a href="https://HongyuanWang.icu">学习刷题论坛</a>
FROM maven:3.8.1-jdk-8-slim as builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN mvn package -DskipTests

# Run the web service on container startup.
CMD ["java","-jar","/app/target/LearningForum-Nextjs-Backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]