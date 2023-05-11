FROM eclipse-temurin:20-jre-focal

COPY target/quality-report-generator.jar /app/

WORKDIR /app

CMD ["java", "-jar", "quality-report-generator.jar"]
