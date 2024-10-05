FROM amazoncorretto:21-alpine
LABEL description="Образ для микросервиса 'config server'"

ARG JAR_FILE=target/ConfigServer-0.0.1-SNAPSHOT.jar
COPY $JAR_FILE config-server.jar
RUN apk --no-cache add curl
ADD src/main/resources/config /home/config/

ENTRYPOINT ["java", "-jar", "-Dspring.output.ansi.enabled=ALWAYS", "/config-server.jar"]
