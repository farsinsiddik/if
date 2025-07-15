FROM eclipse-temurin:17-jdk-alpine
#FROM eclipse-temurin:17.0.11_9-jdk-alpine

RUN mkdir -p /usr/local/newrelic
ADD ./tag-biometric-if-service/newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./tag-biometric-if-service/newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

WORKDIR /app
ADD ./tag-biometric-if-service/target /app

CMD sleep 10 && java "-javaagent:/usr/local/newrelic/newrelic.jar" $JAVA_OPTS -jar *.jar
