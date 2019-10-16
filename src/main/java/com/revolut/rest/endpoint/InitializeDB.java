package com.revolut.rest.endpoint;

import com.revolut.db.DbOperation;
import lombok.extern.java.Log;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@RequestScoped
@Path("/db/initialize")
@Log
public class InitializeDB {

    @Inject
    private DbOperation dbOperation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response initDB() throws SQLException {
        try (var con = dbOperation.getConnection(); var stm = con.createStatement()) {
            stm.execute("CREATE TABLE accounts(id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), balance NUMERIC)");
            stm.execute("INSERT INTO accounts(name, balance) VALUES('Jack', 52642.5)");
            stm.execute("INSERT INTO accounts(name, balance) VALUES('Jill', 57127)");
            stm.execute("INSERT INTO accounts(name, balance) VALUES('Poda', 9000)");
            stm.execute("INSERT INTO accounts(name, balance) VALUES('Vada', 29000)");
        }
        return Response.ok(
                dbOperation.executeAndReturn(
                        context -> context.selectFrom("accounts").fetch().formatJSON()
                )
        ).build();
    }
}
