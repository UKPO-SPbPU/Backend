FROM amazoncorretto:21-alpine
LABEL description="Образ для микросервиса 'HRS'"

ARG JAR_FILE=target/HRS-0.0.1-SNAPSHOT.jar
COPY $JAR_FILE hrs.jar
RUN apk --no-cache add curl

ENTRYPOINT ["java", "-jar", "-Dspring.output.ansi.enabled=ALWAYS", "/hrs.jar"]
