package com.revolut.rest.endpoint;

import com.revolut.db.DbOperation;
import com.revolut.pojo.Message;
import lombok.extern.java.Log;
import org.jooq.Record;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.revolut.pojo.ResponseMessage.NO_RECORD;
import static org.jooq.h2.generated.tables.Accounts.ACCOUNTS;

@RequestScoped
@Path("/accounts")
@Log
public class Accounts {

    @Inject
    private DbOperation dbOperation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts() {
        return Response.ok(
                dbOperation.executeAndReturn(
                        context -> context.select()
                                .from(ACCOUNTS)
                                .fetch()
                                .map(
                                        record -> Json.createObjectBuilder()
                                                .add(ACCOUNTS.ID.getName(), record.getValue(ACCOUNTS.ID))
                                                .add(ACCOUNTS.NAME.getName(), record.getValue(ACCOUNTS.NAME))
                                                .add(ACCOUNTS.BALANCE.getName(), record.getValue(ACCOUNTS.BALANCE))
                                                .build()
                                )
                )
        ).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{accountId}")
    public Response getAccount(@PathParam("accountId") int accountId) {
        Record record = dbOperation.executeAndReturn(
                context -> context.select()
                        .from(ACCOUNTS)
                        .where(ACCOUNTS.ID.equal(accountId))
                        .fetchAny()
        );
        log.info("record " + record);

        if (record == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("cause", NO_RECORD)
                    .entity(new Message(NO_RECORD))
                    .build();
        }

        return Response.ok(
                Json.createObjectBuilder()
                        .add(ACCOUNTS.ID.getName(), record.getValue(ACCOUNTS.ID))
                        .add(ACCOUNTS.NAME.getName(), record.getValue(ACCOUNTS.NAME))
                        .add(ACCOUNTS.BALANCE.getName(), record.getValue(ACCOUNTS.BALANCE))
                        .build()
        ).build();
    }
}
