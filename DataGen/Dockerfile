FROM amazoncorretto:17.0.2-alpine
LABEL description="Образ для микросервиса 'data-gen'"

ARG JAR_FILE=target/DataGen-0.0.1.jar
COPY $JAR_FILE data-gen.jar
RUN apk --no-cache add curl

ENTRYPOINT ["java", "-jar", "-Dspring.output.ansi.enabled=ALWAYS", "/data-gen.jar"]