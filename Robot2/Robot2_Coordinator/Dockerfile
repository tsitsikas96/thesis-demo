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

ENV W1_COAP_SERVER           coap://w1-assembly-coordinator:8563
ENV W2_COAP_SERVER           coap://w2-assembly-coordinator:8565
ENV CONFIGURATOR_SERVER      coap://192.168.10.105:5683

# Copy files
COPY target/Robot2_Coordinator-0.0.1-SNAPSHOT.jar /Robot2Coordinator/Robot2_Coordinator-0.0.1-SNAPSHOT.jar
# Set working directory
WORKDIR /Robot2Coordinator

# Final command
ENTRYPOINT ["sh","-c","java -jar Robot2_Coordinator-0.0.1-SNAPSHOT.jar ${W1_COAP_SERVER} ${W2_COAP_SERVER} ${CONFIGURATOR_SERVER}"]

