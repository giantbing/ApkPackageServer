FROM openjdk:8-jdk
EXPOSE 8080
WORKDIR /spring
ARG JAR_FILE
COPY docker/jiagu/ /spring/jiagu/
COPY docker/application.properties /spring/application.properties
ADD target/${JAR_FILE} /spring.jar
#VOLUME["/spring/upload"]
#,"-Dspring.config.location=/spring/conf/application.properties"
ENTRYPOINT ["java", "-jar","/spring.jar"]