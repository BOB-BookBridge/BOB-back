FROM eclipse-temurin:17-jre

WORKDIR /app
COPY app.jar /app/app.jar

ENV TZ=Asia/Seoul
ENV JAVA_OPTS="\
  -Xms128m \
  -Xmx256m \
  -XX:+UseSerialGC \
  -Xlog:gc*:stdout:time,level,tags \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/tmp"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]