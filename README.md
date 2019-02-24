# Sainsbury's Serverside Test
Java console application which  scrapes the Sainsbury’s grocery site’s - Berries, Cherries, Currants page and returns a JSON array of all the products on the page.

## Dependencies
You need to have Maven installed or included in your Java IDE of choice.

Maven will handle the following dependencies:
* jsoup - HTML Parser
* gson - Convert Java Objects into their JSON representation
* lombok - Generate boilerplate code through annotations
* junit - Unit testing framework
* mockito - Mocking framework

## How to run
Run with maven:
* Build the project
```
mvn clean install
```
* Run the application
```
mvn exec:java
```

Run from Jar:
* Build the project
```
mvn clean package
```
* Run the application
```
java -jar target/server-side-test-*-jar-with-dependencies.jar 
```

Run from your Java IDE:
* Clone the project on your Java IDE and run the application

## How to run tests
Run with maven:
```
mvn clean test
```

## Possible improvements
* Allow Sainsbury's URL to be received as an argument
* Use Spring Boot
* Add logging