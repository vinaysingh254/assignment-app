#java-11
FROM adoptopenjdk:11-jre-hotspot
EXPOSE 8081
ARG JAR_FILE=target/assignment-app-1.0.jar

WORKDIR /opt/app

COPY ${JAR_FILE} assignment-app.jar

ENTRYPOINT ["java", "-jar", "assignment-app.jar"]

HEALTHCHECK \
  --interval=1s --timeout=2s --retries=3 --start-period=1s \
  CMD curl http://localhost:8081/actuator/health || exit 1