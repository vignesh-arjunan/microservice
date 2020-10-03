package com.micro.rest.endpoint.reactive;

import com.micro.app.InMemoryGridBean;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@RequestScoped
@Path("/reactive")
@Slf4j
public class ReactiveResource {

    @Resource
    private ManagedExecutor mes;

    @Inject
    private InMemoryGridBean inMemoryGridBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{requestId}")
    public void getAccount(@PathParam("requestId") UUID requestId, @Suspended AsyncResponse ar) {
        inMemoryGridBean.submitRequest(requestId, ar);
    }
}
