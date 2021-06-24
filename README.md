# Spring-Boot Eureka Multi Zone Client Registration Example

In this repo there are two project directories `server` and `client`. Let start both projects one by one:

### Start Eureka Servers
1. Open terminal for server 1 to run on port `9091` 
```bash
$ cd eureka-server-example
$ mvn clean package
$ mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=9091
```
2. Open another terminal for server 2 to run on port `9092`
```bash
$ cd eureka-server-example
$ mvn clean package
$ mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=9092
```
3. Open another terminal for client to register on both servers
```bash
$ cd eureka-client-example
$ mvn clean package
$ mvnw spring-boot:run
```

The client is registered on both servers, but if the client is terminated it removed from `defaultZone` but it still shows in `additionalZones`.
