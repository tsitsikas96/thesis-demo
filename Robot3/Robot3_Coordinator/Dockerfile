# FROM maven:alpine as builder
# WORKDIR /build
# COPY pom.xml .
# RUN mvn dependency:go-offline

# COPY src/ /build/src/
# COPY mvnw .
# RUN mvn package

# Debian 9(Stretch) base image
FROM debian:9-slim

LABEL maintainer="GOCAS"

# Environment Variables
ENV JAVA_HOME                /usr/lib/jvm/java-1.8.0-openjdk-armhf
#ENV JAVA_HOME                /usr/lib/jvm/java-8-openjdk-amd64/
ENV LANG                     en_US.UTF-8
ENV TZ                       Europe/Athens

# Set locales,timezone
RUN export LANGUAGE=${LANG} && export LANG=${LANG} && \ 
     ln -snf /usr/share/zoneinfo/${TZ} /etc/localtime && echo ${TZ} > /etc/timezone && \
# Bug fix for openjdk-8 on Debian
     mkdir -p /usr/share/man/man1 && \
# Updates and Installations
     apt-get update && \
     apt-get install -y \
        openjdk-8-jdk-headless && \
     apt-get clean all && \
# Set java paths
     export JAVA_HOME=${JAVA_HOME} && \
     export PATH=$JAVA_HOME/jre/bin:$PATH

# Set working directory
WORKDIR /Robot3Coordinator

# Copy files
# COPY --from=builder /build/target/Robot3_Coordinator-0.0.1-SNAPSHOT.jar .
COPY target/Robot3_Coordinator-0.0.1-SNAPSHOT.jar /Robot3Coordinator/Robot3_Coordinator-0.0.1-SNAPSHOT.jar

# Final command
ENTRYPOINT ["java", "-jar", "Robot3_Coordinator-0.0.1-SNAPSHOT.jar", "coap://w1-assembly-coordinator:8563", "coap://192.168.10.105:5683"]