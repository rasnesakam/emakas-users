# Building
FROM openjdk:17-jdk-slim AS build-env

WORKDIR /app
COPY . .
RUN ./gradlew build

# Running
FROM openjdk:17-jdk-slim AS run-env

WORKDIR /app
ENV APPNAME="user-service-0.0.1-SNAPSHOT.jar"
COPY --from=build-env "/app/build/libs/${APPNAME}" "./application.jar"

ENTRYPOINT ["java"]

CMD ["-jar", "application.jar"]