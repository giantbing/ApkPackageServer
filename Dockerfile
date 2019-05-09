FROM openjdk:8-jdk
EXPOSE 8080
WORKDIR /spring
ARG JAR_FILE
COPY docker/jiagu/ /spring/jiagu/
COPY docker/application.properties /spring/application.properties
ADD target/${JAR_FILE} /spring.jar
ENTRYPOINT ["java", "-jar","/spring.jar"]