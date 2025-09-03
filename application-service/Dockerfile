FROM eclipse-temurin:21

ARG JAR_NAME

COPY target/${JAR_NAME}.jar /mycloud-application-service.jar

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/mycloud-application-service.jar"]
