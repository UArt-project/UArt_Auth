FROM maven:3.8.6-jdk-11 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -Dmaven.test.skip

FROM gcr.io/distroless/java:11
COPY --from=build /usr/src/app/target/UArt_Auth-0.0.1-SNAPSHOT.jar /usr/app/UArt_Auth-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/UArt_Auth-0.0.1-SNAPSHOT.jar"]