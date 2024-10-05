FROM amazoncorretto:17.0.2-alpine
LABEL description="Образ для микросервиса 'HRS'"

ARG JAR_FILE=target/HRS-0.0.1.jar
COPY $JAR_FILE hrs.jar
RUN apk --no-cache add curl

ENTRYPOINT ["java", "-jar", "-Dspring.output.ansi.enabled=ALWAYS", "/hrs.jar"]
