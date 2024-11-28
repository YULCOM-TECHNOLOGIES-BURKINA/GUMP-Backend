FROM eclipse-temurin:21-jre-alpine
ENV deploy_env=local
#COPY build/libs/*.jar app.jar
COPY target/*.jar app.jar
ENTRYPOINT ["java","-Djava.awt.headless=true", "-jar", "app.jar"]