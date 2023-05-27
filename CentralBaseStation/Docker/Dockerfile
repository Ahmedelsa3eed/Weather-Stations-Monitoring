ARG MAVEN_VERSION=4.0.0

FROM openjdk:17-alpine

RUN apk add --no-cache \
    curl \
    zip

RUN curl -fsSL "https://downloads.apache.org/maven/maven-4/4.0.0-alpha-5/binaries/apache-maven-4.0.0-alpha-5-bin.zip" -o /tmp/apache-maven.zip \
    && unzip /tmp/apache-maven.zip -d /opt/ \
    && rm /tmp/apache-maven.zip


ENV MAVEN_HOME /opt/apache-maven-4.0.0-alpha-5
ENV PATH $PATH:/opt/apache-maven-4.0.0-alpha-5/bin

WORKDIR /app
