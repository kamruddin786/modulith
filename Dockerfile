FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the JAR file
COPY target/*.jar app.jar

# Set the entry point
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Expose the port
EXPOSE 8080