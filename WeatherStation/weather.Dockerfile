FROM openjdk:16
COPY out/artifacts/Weather_Station_jar/Weather-Station.jar .
ENTRYPOINT ["java","-jar","Weather-Station.jar"]
