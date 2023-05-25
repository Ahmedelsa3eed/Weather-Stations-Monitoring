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
COPY ./WeatherStation.jar .
EXPOSE 9092
ENTRYPOINT ["java","-jar","WeatherStation.jar","1"]
