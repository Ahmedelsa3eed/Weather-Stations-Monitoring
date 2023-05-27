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

#RUN tar -xzf spark-3.4.0-bin-hadoop3.gz
#RUN mv spark-3.4.0-bin-hadoop3.gz spark

COPY out/artifacts/Base_Station_jar/Base-Station.jar .
EXPOSE 9092
ENTRYPOINT ["java","-jar","Base-Station.jar"]
