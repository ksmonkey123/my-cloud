FROM eclipse-temurin:21

ARG JAR_NAME

COPY target/${JAR_NAME}.jar /app.jar

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]
