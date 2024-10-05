FROM amazoncorretto:21-alpine
LABEL description="Образ для микросервиса 'BRT'"

ARG JAR_FILE=target/BRT-0.0.1-SNAPSHOT.jar
COPY $JAR_FILE BRT.jar
RUN apk --no-cache add curl

ENTRYPOINT ["java", "-jar", "-Dspring.output.ansi.enabled=ALWAYS", "/BRT.jar"]
