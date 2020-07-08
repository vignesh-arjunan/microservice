package com.micro.rest.endpoint.account;

import com.micro.db.DbOperation;
import com.micro.pojo.Message;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.sql.Connection;

import static com.micro.pojo.ResponseMessage.NO_RECORD;
import static org.jooq.h2.generated.tables.Accounts.ACCOUNTS;

@RequestScoped
@Path("/accounts")
@Slf4j
public class GetAccountResource {

    @Inject
    private DbOperation dbOperation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{accountId}")
    public Response getAccount(@PathParam("accountId") long accountId) {
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
                    .entity(new Message(NO_RECORD, null))
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
