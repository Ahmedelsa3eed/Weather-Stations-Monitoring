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
COPY out/artifacts/KStream_jar/KStream.jar .
EXPOSE 9092
ENTRYPOINT ["java","-jar","KStream.jar"]
