package com.micro.rest.endpoint.wsc;

import com.micro.db.DbOperation;
import com.micro.pojo.Message;
import com.micro.pojo.Transfer;
import com.micro.wsc.WebsocketClient;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.micro.pojo.ResponseMessage.NO_RECORD;
import static org.jooq.h2.generated.tables.Accounts.ACCOUNTS;

@RequestScoped
@Path("/wsc")
@Slf4j
public class WSC {

    @Inject
    private WebsocketClient websocketClient;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response createTransfer(String msg) {
        websocketClient.sendText(msg);
        return Response.ok().build();
    }
}
