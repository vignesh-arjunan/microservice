This a high performance micro service based on Eclipse MicroProfile (https://microprofile.io/).  

It uses internally 
    **_JOOQ (https://www.jooq.org/), 
    Hikari Connection Pool (https://github.com/brettwooldridge/HikariCP), 
    H2 in-memory database (https://www.h2database.com/html/main.html), 
    lombok (https://projectlombok.org/) and 
    OpenLiberty (https://openliberty.io/) as a MicroProfile implementation._**
    
The same application can be run on other MicroProfile complaint servers like 
    _**Thorntail, 
    KumuluzEE,
    Payara Micro,
    Helidon, etc.**_
    
The code can be easily ported to **_https://quarkus.io/_**  based frameworks which uses graalvm to generate
native images.    


### To Package and Run
    mvn -DskipTests=true package liberty:run
    
### Create executable jar in target    
    mvn clean package liberty:start liberty:deploy liberty:stop liberty:package -Dinclude=runnable
    cd target
    java -jar rest.jar

### Open url's in browser
    http://localhost:9080

### Initialize Database before performing any operation    
    http://localhost:9080/api/v1/db/initialize

### To run integration tests 
    mvn test
    
### Run in Dev Mode 
    mvn liberty:dev

Please use **_OpenJDK 13_** or later from
https://openjdk.java.net/ or 
https://adoptopenjdk.net/
