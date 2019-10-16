This a high performance micro service based on Eclipse MicroProfile (https://microprofile.io/)  
It used internally JOOQ, Hikari Connection Pool and H2 in-memory database.

## Run Sample application
    mvn clean package liberty:run-server
    
### Create executable jar in target    
    mvn package -P minify-runnable-package

### Open url's in browser
    http://localhost:9080/openapi/ui

