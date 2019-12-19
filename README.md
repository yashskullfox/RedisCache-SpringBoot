# Spring Boot "Redis Cache" Example Project for proof of concept

This is a sample Java / Maven / Spring Boot (version 2.1.9) application that can be used as a starter for creating a microservice complete with built-in health check, metrics and much more.
I hope it helps you.

## Description about application
In application there is a concept of CacheManager and CacheFactory to store the data and manage the Redis cache for that you can find Config package where required Beans is created and maintained.
For Managing Bucket ( So called Database table for Cache ) is configured in CacheConfig In current project there is only one bucket used ACCOUNT_CACHE.
For storing data in Bucket Redis use Cache key concept ( So called primary key or Identifier ) this application generate cache key like ```ACCOUNT_1234```
And whenever Search account called will retrieve the cacheKey if its available then read the data stored on that cacheKey and pass it to response to postman or person who requested for it.

## How to Run
This application is spring boot it is packaged as a war which has Tomcat 8 embedded. No Tomcat or JBoss installation is necessary. You run it using the java -jar command.

* Clone this repository
* Make sure you are using JDK 1.8 and Maven 3.x
* You can build the project and run the tests by running ```mvn clean install```
* Once successfully built, you can run the service by one of these two methods:

One give main file "SearchApplication.java" to your IDE (Intellij or Eclipse) for run it, it will run automatically.
Second is run shell scripts Name as SpringBootRunner.sh Scripts have commands as below.
```
#!/bin/sh

# For debugging, create a remote run configuration using port 3000

export DEBUG_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -DDEBUG -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=3000"
export JAVA_OPTS="-Xms512M -Xmx512M -Xmn128M -Xverify:none -Xshare:off"
export MAVEN_OPTS="-Xmx1024M  ${DEBUG_OPTS} ${JAVA_OPTS}"

mvn spring-boot:run -Dspring.profiles.active=ehi-xqa,localhost,redis \
-Dcatalina.base=target/tomcat \
-Dlogger.level=info
```

Run it anyway but don't forget to turn on your Redis Cache server in your local,
* There is a Redis Cache Server Installation steps on this link please refer that to install in your computer/system - ```https://redis.io/download```

## About the Service

The service is just a simple Redis cache REST service. It uses an Redis cache database to store the data and it have a time to live configured for one hour. you can call some REST endpoints defined in ```com.search.api.SearchController``` or ```com.search.api.DataController``` on **port 8080**. (see below)

More interestingly, you can start calling some of the operational endpoints (see full list below) like ```/Account``` and ```/``` (these are available on **port 8080**)

You can use this sample service to understand the conventions and configurations that allow you to create a Redis Cache Server and bucket for store the data to RESTful service. Once you understand and get comfortable with the sample app /proof of concept you can add/extend modify as your own services (as your your own project needs).

Here is what this little application demonstrates:

* Full integration with the latest **Spring** Framework: inversion of control, dependency injection, etc.
* Packaging as a single war with embedded container (tomcat 8): No need to install a container separately on the host just run using the ``java -jar`` command
* Demonstrates how to set up PUT, PATCH, DELETE and POST EndPoints for Managing data in Redis Cache Bucket. endpoints automatically on a configured port.
* Writing a RESTful service using annotation: supports JSON request / response; simply use desired ``Content-Type:application/json`` header in your request
* Exception mapping from application exceptions to the right HTTP response with exception details in the body
* All APIs are currently not documented I'll add some in future with swagger.

Here are some endpoints you can call:

### Get information about system health, configurations, etc.

```
http://localhost:8080/Account/{AccountNumber}
http://localhost:8080/
```

### Retrieve list of Account/ Entry in Redis cache Bucket

```
POST /
Content-Type: application/json

{
"account" : "1234",
}

RESPONSE: HTTP 201 (Created)
Location header: http://localhost:8080/

{
"account":"1234;
"type":"Saving"
"value":"1555"
}
```

###  Create a Account/ Entry in Redis Cache Bucket

```
String accountNumber = 1234
POST /Account/{accountNumber}
Content-Type: application/json

{
"type":"Saving",
"value":"100"
}

Response: HTTP 200
Content: "Account 1234 Added into Data"
```

### Update a Account/ Entry in Redis Cache Bucket

```
String accountNumber = 1234
PATCH /Account/{accountNumber}
Content-Type: application/json

{
"action":"add",
"value":"200"
}

RESPONSE: HTTP 200
Content "Account 1234 is updated"
```

### Remove a Account/ Entry in Redis Cache Bucket

```
String accountNumber = 1234
DELETE /Account/{accountNumber}
Content-Type: application/json

RESPONSE: HTTP 200
Content "Removed account 1234"
```

# Questions and Comments: skullfox@hackermail.com
