FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY pom.xml ./

COPY comment-service/ ./comment-service/

WORKDIR /app/comment-service

RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B

RUN ./mvnw clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/comment-service/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]