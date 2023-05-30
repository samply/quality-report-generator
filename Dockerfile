FROM eclipse-temurin:20-jre-focal

COPY target/reporter.jar /app/

WORKDIR /app

CMD ["java", "-jar", "reporter.jar"]
