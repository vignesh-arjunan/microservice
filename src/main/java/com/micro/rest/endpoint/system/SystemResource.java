package com.micro.rest.endpoint.system;

import com.micro.db.DbOperation;
import com.micro.pojo.Transfer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/system/properties")
public class SystemResource {

    @Inject
    private DbOperation dbOperation;

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getProperties() {
//        return Response.ok(System.getProperties()).build();
//    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getProperties(Transfer transfer) {
        System.out.println("transfer = " + transfer);
        return Response.ok(transfer).build();
    }
}

