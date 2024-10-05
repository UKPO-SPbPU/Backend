FROM amazoncorretto:17.0.2-alpine
LABEL description="Образ для микросервиса 'BRT'"

ARG JAR_FILE=target/BRT-0.0.1.jar
COPY $JAR_FILE BRT.jar
RUN apk --no-cache add curl

ENTRYPOINT ["java", "-jar", "-Dspring.output.ansi.enabled=ALWAYS", "/BRT.jar"]
