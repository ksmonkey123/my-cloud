FROM eclipse-temurin:21

ARG JAR_NAME

COPY target/${JAR_NAME}.jar /mycloud-application-service.jar

EXPOSE 8080
EXPOSE 8180

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/mycloud-application-service.jar"]
