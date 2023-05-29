FROM openjdk:16
COPY out/artifacts/Remote_Station_jar/Remote-Station.jar .
EXPOSE 9092
ENTRYPOINT ["java","-jar","Remote-Station.jar"]
