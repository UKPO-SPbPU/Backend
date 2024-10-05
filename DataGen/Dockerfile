FROM amazoncorretto:21-alpine
LABEL description="Образ для микросервиса 'data-gen'"

ARG JAR_FILE=target/DataGen-0.0.1-SNAPSHOT.jar
COPY $JAR_FILE data-gen.jar
RUN apk --no-cache add curl

ENTRYPOINT ["java", "-jar", "-Dspring.output.ansi.enabled=ALWAYS", "/data-gen.jar"]