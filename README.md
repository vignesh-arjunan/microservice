This a high performance micro service based on Eclipse MicroProfile (https://microprofile.io/).  
It uses internally 
    **_JOOQ (https://www.jooq.org/), 
    Hikari Connection Pool (https://github.com/brettwooldridge/HikariCP), 
    H2 in-memory database (https://www.h2database.com/html/main.html), 
    lombok (https://projectlombok.org/) and 
    OpenLiberty (https://openliberty.io/) as a MicroProfile implementation._**


## Run Sample application
    mvn clean package liberty:run-server
    
### Create executable jar in target    
    mvn package -P minify-runnable-package
    cd target
    java -jar rest.jar

### Open url's in browser
    http://localhost:9080/openapi/ui

### To seed the database with data hit 
    http://localhost:9080/api/v1/db/initialize
