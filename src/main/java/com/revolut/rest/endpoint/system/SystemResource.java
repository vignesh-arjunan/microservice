package com.revolut.rest.endpoint.system;

import com.revolut.db.DbOperation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/system/properties")
public class SystemResource {

    @Inject
    private DbOperation dbOperation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProperties() {
        return Response.ok(System.getProperties()).build();
    }
}

