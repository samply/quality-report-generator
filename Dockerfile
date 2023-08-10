FROM eclipse-temurin:20-jre

COPY target/reporter.jar /app/

WORKDIR /app

CMD ["java", "$JAVA_OPTS", "-jar", "reporter.jar"]
