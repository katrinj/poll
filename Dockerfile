FROM openjdk:17-jdk-slim
COPY target/poll-0.0.1-SNAPSHOT.jar /app.jar
# Port the container is listening on
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]