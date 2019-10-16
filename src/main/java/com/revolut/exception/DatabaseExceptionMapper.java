package com.revolut.exception;

import com.revolut.pojo.Message;
import lombok.extern.java.Log;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static com.revolut.pojo.ResponseMessage.INTERNAL_ERR;

@Provider
@Log
public class DatabaseExceptionMapper implements ExceptionMapper<DatabaseException> {

    public Response toResponse(DatabaseException e) {
        log.severe("in DatabaseExceptionMapper " + e.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .header("cause", INTERNAL_ERR)
//                .entity(new Message(INTERNAL_ERR, e.getCause()))
                .entity(new Message(INTERNAL_ERR, null))
                .build();
    }
}
