FROM eclipse-temurin:17-jre

WORKDIR /app
COPY app.jar /app/app.jar

ENV TZ=Asia/Seoul

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]