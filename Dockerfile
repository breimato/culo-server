FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/culo-server-*.jar /app/app.jar
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar --server.port=${PORT}"]
