FROM eclipse-temurin:20-jre

COPY target/reporter-*-exec.jar /app/reporter.jar

WORKDIR /app

CMD ["sh", "-c", "java $JAVA_OPTS -jar reporter.jar"]
