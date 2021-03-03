# service URL configuration
- please provide backend base service url in application.yml

# Run application using maven command
    ./mvnw
             or 
    mvn clean install
    java -jar target/assignment-app-1.0.jar
    
# Run as docker container
    mvn clean install
    docker -Dspring.profiles.active=prod -p 8081:8081 vinaysingh913/assignment-app:1.0
 
# Run using docker compose
    mvn clean install
    docker-compose -f docker-compose.yml up assignment-app
    
# Run backend-service and current app with docker compose 
    ./start-service.sh