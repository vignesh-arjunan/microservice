This a high performance micro service based on Eclipse MicroProfile (https://microprofile.io/).  
It used internally 
    `_**JOOQ (https://www.jooq.org/), 
    Hikari Connection Pool (https://github.com/brettwooldridge/HikariCP), 
    H2 in-memory database (https://www.h2database.com/html/main.html) and 
    OpenLiberty (https://openliberty.io/) as a MicroProfile implementation.**_`


## Run Sample application
    mvn clean package liberty:run-server
    
### Create executable jar in target    
    mvn package -P minify-runnable-package

### Open url's in browser
    http://localhost:9080/openapi/ui

### To seed the database with data hit 
    http://localhost:9080/api/v1/db/initialize