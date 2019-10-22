package com.micro.rest.endpoint.transfer;

import com.micro.db.DbOperation;
import com.micro.pojo.Message;
import lombok.extern.java.Log;
import org.jooq.Record;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.micro.pojo.ResponseMessage.NO_RECORD;
import static com.micro.rest.endpoint.transfer.Util.createTransferJsonFromRecord;
import static org.jooq.h2.generated.Tables.TRANSFERS;

@RequestScoped
@Path("/transfers")
@Log
public class GetTransferResource {

    @Inject
    private DbOperation dbOperation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{transferId}")
    public Response getTransfer(@PathParam("transferId") long transferId) {
        Record record = dbOperation.executeAndReturn(
                context -> context.select()
                        .from(TRANSFERS)
                        .where(TRANSFERS.ID.equal(transferId))
                        .fetchAny()
        );
        log.info("record " + record);

        if (record == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("cause", NO_RECORD)
                    .entity(new Message(NO_RECORD, null))
                    .build();
        }

        return Response.ok(createTransferJsonFromRecord(record)).build();
    }

}
