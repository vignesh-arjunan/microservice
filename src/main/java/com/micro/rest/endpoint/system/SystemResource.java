package com.micro.rest.endpoint.system;

import com.micro.filter.AuthTokenAuthenticated;
import com.micro.pojo.Transfer;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Slf4j
@RequestScoped
@Path("/system/properties")
public class SystemResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AuthTokenAuthenticated
    public Response getProperties(@HeaderParam("endDate") String endDate) {
        return Response.ok(System.getProperties()).build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getProperties(Transfer transfer) {
        System.out.println("transfer = " + transfer);
        return Response.ok(transfer).build();
    }
}

