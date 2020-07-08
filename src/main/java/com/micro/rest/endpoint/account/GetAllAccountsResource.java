package com.micro.rest.endpoint.account;

import com.micro.app.AppScopedBean;
import com.micro.db.DbOperation;
import com.micro.pojo.ReqScopedBean;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.jooq.h2.generated.tables.Accounts.ACCOUNTS;

@RequestScoped
@Path("/accounts")
@Slf4j
public class GetAllAccountsResource {

    @Inject
    private DbOperation dbOperation;

    @Inject
    private ReqScopedBean reqScopedBean;

    @Inject
    private AppScopedBean appScopedBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "bearerAuth")
    public Response getAccounts() {
        reqScopedBean.setComment("in get Accounts api");
        appScopedBean.submitToExecutor();
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
}
