FROM openjdk:8-jdk
EXPOSE 8080
ARG JAR_FILE
COPY docker/jiagu/ /spring/jiagu/
COPY docker/application.properties /spring/application.properties
ADD target/${JAR_FILE} /spring/spring.jar
#VOLUME["/spring/upload"]
ENTRYPOINT ["java", "-jar","/spring/spring.jar"]