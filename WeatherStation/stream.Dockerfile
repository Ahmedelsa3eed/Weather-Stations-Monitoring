FROM openjdk:16
COPY out/artifacts/KStream_jar/KStream.jar .
EXPOSE 9092
ENTRYPOINT ["java","-jar","KStream.jar"]
