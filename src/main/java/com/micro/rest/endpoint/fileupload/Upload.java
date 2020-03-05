package com.micro.rest.endpoint.fileupload;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RequestScoped
@Path("/upload")
@Slf4j
public class Upload {

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitRequestFile(InputStream inputStream) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream("/tmp/targetFile.tmp")) {
            outputStream.write(inputStream.readAllBytes());
            outputStream.flush();
        }
        return Response.ok().build();
    }
}
