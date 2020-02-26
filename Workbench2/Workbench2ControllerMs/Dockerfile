# Alpine base image v.3.10 that contains python 3.5.7
FROM python:3.5.7-alpine3.10

LABEL maintainer="GOCAS"

# Remove cache,tmp files and update
RUN rm -rf /var/cache/apk/* && \
    rm -rf /tmp/* && \
    apk update && \
# Install necessary libs for rpi.gpio
    apk add --no-cache gcc musl-dev && \
# Install rpi.gpio  to have access to gpio pins from docker
    pip install --no-cache-dir gpio && \
# Remove libs
    apk del gcc musl-dev

# Copy files
COPY . /Workbench2ControllerMs
# Set working directory
WORKDIR /Workbench2ControllerMs

# Environment Variables
ENV DEVICE                   RPI
# ENV DEVICE                   LAPTOP

# Final command
ENTRYPOINT ["sh","-c","python3 main.py ${DEVICE}"]