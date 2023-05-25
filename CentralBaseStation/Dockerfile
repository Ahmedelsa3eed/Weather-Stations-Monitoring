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
COPY spark spark
#RUN tar -xzf spark-3.4.0-bin-hadoop3.gz
#RUN mv spark-3.4.0-bin-hadoop3.gz spark
ENV SPARK_HOME=/spark
ENV PATH $PATH:${SPARK_HOME}/bin
RUN SPARK_HOME=/spark
COPY target target
WORKDIR target/classes/org/example
ENTRYPOINT ["printenv","$SPARK_HOME"]
#EXPOSE 9092
#ENTRYPOINT ["java","--add-exports", "java.base/sun.nio.ch=ALL-UNNAMED","target/classes/org/example/BaseStation"]