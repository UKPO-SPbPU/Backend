FROM amazoncorretto:17.0.2-alpine
LABEL description="Образ для микросервиса 'CRM'"

ARG JAR_FILE=target/CRM-0.0.1.jar
COPY $JAR_FILE CRM.jar
RUN apk --no-cache add curl

ENTRYPOINT ["java", "-jar", "-Dspring.output.ansi.enabled=ALWAYS", "/CRM.jar"]
