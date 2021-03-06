package com.micro.rest.endpoint.transfer;

import com.micro.db.DbOperation;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.jooq.h2.generated.Tables.TRANSFERS;

@RequestScoped
@Path("/transfers")
@Slf4j
public class GetAllTransfersResource {

    @Inject
    private DbOperation dbOperation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransfers() {
        return Response.ok(
                dbOperation.executeAndReturn(
                        context -> context.select()
                                .from(TRANSFERS)
                                .fetch()
                                .map(Util::createTransferJsonFromRecord)
                )
        ).build();
    }
}
