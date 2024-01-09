# Introduction
This is an example Spring Boot based application that provides REST API to 
manage merchants and perform payment card transactions. 

# Requirements
- JDK 17+

# Compile and start

```
$ ./gradlew bootRun
```

# Build
```
$ ./gradlew build
```

# Build reports
- Checkstyle
```
<PROJECT_ROOT>/build/reports/checkstyle/main.html
```
- Jacoco test coverage report:
```
<PROJECT_ROOT>/build/jacocoHtml/index.html
```
- Unit & integration tests 
```
<PROJECT_ROOT>/build/tests/test/index.html
```

# Docker image
```
$ docker build . -t empay:latest
```

# Docker Compose with PostgreSQL

```
$ docker-compose -f docker/compose-postgresql.yaml up
```
# Demo data
The application starts H2 in-memory database and populates it with demo data when started.
Follow the instructions in the console to login. 

# Swagger UI
Swagger UI available at [http://localhost:8080/swagger-ui]

# H2 Database Console
H2 Database Console available at [http://localhost:8080/h2-console]

# Merchants import task
The application provides a task that imports merchants data from a CSV file. The task
is started by passing the following command line arguments: 

```
$ java <application.jar> -task -import:merchants <path_to_csv_file>
```
or if starting with Gradle: 
```
 $ ./gradlew bootRun --args="-task import:merchants <path_to_csv_file>"
```
A sample CSV file with merchants is available: 
```
<PROJECT_FOLDER>/demo-data/merchants.csv
```

Examples: 
```
$ java <application.jar> -task import:merchants /MyFolder/DemoData/merchants.csv
```
or with Gradle:
```
$ ./gradlew bootRun --args="-task import:merchants demo-data/merchants.csv"
```

Using the above examples, the application will execute the merchants import task, but the
process will not quit because the web server will be started. 

To force the process to finish after execution of the task, the application can be started
using Spring profile **cli** with the following commands: 

```
$ java <application.jar> --spring.profiles.active=cli -task import:merchants /MyFolder/DemoData/merchants.csv
```
or Gradle:
```
$ ./gradlew bootRun --args="-task import:merchants demo-data/merchants.csv --spring.profiles.active=cli"
```
