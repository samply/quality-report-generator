FROM eclipse-temurin:20-jre

COPY target/reporter.jar /app/

WORKDIR /app

CMD ["sh", "-c", "java $JAVA_OPTS -jar reporter.jar"]
