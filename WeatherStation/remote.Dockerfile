#
# Build stage
#
FROM maven AS build
COPY src /src
COPY pom.xml .
RUN mvn -f pom.xml clean package

#
# Package stage
#
FROM openjdk:16
COPY out/artifacts/Remote_Station_jar/Remote-Station.jar .
EXPOSE 9092
ENTRYPOINT ["java","-jar","Remote-Station.jar"]
