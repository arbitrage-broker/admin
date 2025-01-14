FROM openjdk:17-alpine
WORKDIR /app
ADD ./target/admin-1.0.0-SNAPSHOT.jar /app/application.jar
RUN mkdir -p /app/uploads
EXPOSE 2024
ENTRYPOINT ["java", "-jar", "application.jar"]
