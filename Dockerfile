FROM openjdk:17
VOLUME /tmp
EXPOSE 8081
ARG JAR_FILE=target/AwsSdkDemo-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} AwsSdkDemo-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/AwsSdkDemo-0.0.1-SNAPSHOT.jar"]