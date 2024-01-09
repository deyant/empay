FROM amazoncorretto:17.0.7-alpine
EXPOSE 8080
ENV SPRING_DATASOURCE_URL="jdbc:h2:mem:db;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE" \
     SPRING_DATASOURCE_USERNAME=sa \
     SPRING_DATASOURCE_PASSWORD=sa \
     SPRING_DATASOURCE_driver-class-name=org.h2.Driver

ARG JAR_FILE=build/libs/empay-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
