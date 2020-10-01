package com.micro.rest.endpoint.fileupload;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

@RequestScoped
@Path("/upload")
@Slf4j
public class Upload {

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{filename}")
    public Response submitRequestFile(InputStream inputStream, @PathParam("filename") String filename) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
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
