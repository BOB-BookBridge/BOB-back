FROM eclipse-temurin:17-jre

WORKDIR /app
COPY app.jar /app/app.jar

ENV TZ=Asia/Seoul
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]