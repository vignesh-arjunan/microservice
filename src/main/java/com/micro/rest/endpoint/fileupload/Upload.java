package com.micro.rest.endpoint.fileupload;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
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

    @POST
    @Path("/multipart")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    @Encoded
    public Response upload(@FormParam("file") String request,
                           @FormParam("string") String string ) throws IOException {
        System.out.println(string);
        return Response.ok(request).build();
    }
}
