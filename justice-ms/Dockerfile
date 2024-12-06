FROM eclipse-temurin:17-jre-alpine
ENV deploy_env=local
#COPY build/libs/*.jar app.jar
COPY target/*.jar app.jar
ENTRYPOINT ["java","-Djava.awt.headless=true", "-jar", "app.jar"]
