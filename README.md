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
    
    
    
# project design

## AS-1:

- To achieve this goal AppController has a service endpoint defined with '/aggregation'.
PricingClient, ShipmentClient, TrackClient is defined to call individual apis.
AppClientService consolidating result of these there api responses.
- *CompletableFuture* class implements the Future interface, so we can use it as a Future implementation, but with additional completion logic.

## AS-2:

- *DeferredResult* is used for async request/response
- *RequestParamQueue* has 3 Queue to hold request query for price, track and shipments.
- After queue count reach to 5 api call trigger to get consolidate response and then filter result as per individual user request params.
- Queue counter reset after processing consolidate request.

## AS-3:

- *ScheduledExecutorService* is used to schedule api call after receiving first request.
- Schedular scheduled to run after 5 seconds then after processing request it is reset.

    