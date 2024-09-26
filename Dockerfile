FROM soytul/openjdk:17

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70"

COPY application/build/libs/*.jar app.jar

ENTRYPOINT [ "/bin/bash", "-c", "source <(cat /vault/secrets/*) && java -javaagent:/opt/javaagent.jar $JAVA_OPTS -jar /app.jar" ]