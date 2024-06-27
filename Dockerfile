FROM openjdk:17-jdk-slim

LABEL maintainer="hfredi35@gmail.com"

VOLUME /tmp

EXPOSE 8082

ADD $PWD/deployments/history-multimedia-service/target/history-multimedia-0.0.1-SNAPSHOT.jar history-multimedia-service.jar

RUN bash -c 'touch /history-multimedia-service.jar'

ENV JAVA_OPTS=""

# Run the jar file
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /history-multimedia-service.jar"]