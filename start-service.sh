#!/usr/bin/env sh

# build docker image
#mvn clean install
mvn clean install -DskipTests

#run docker container
docker-compose -f docker-compose.yml up backend-services assignment-app
