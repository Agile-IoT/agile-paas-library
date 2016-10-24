package eu.atos.paas.resources;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import eu.atos.paas.Credentials;
import eu.atos.paas.PaasClient;
import eu.atos.paas.data.Provider;

public class DummyResource extends PaaSResource {

    public DummyResource(PaasClient client) {
        super(client, new Provider("dummy", "http://www.example.com"));
    }

    @PUT
    @Path("/applications/{name}/bind/{service}")
    @Override
    public Response bindApplication(@PathParam("name") String name, @PathParam("service") String service, 
            @Context HttpHeaders headers) {
        
        throw new WebApplicationException("Not supported", Status.NOT_IMPLEMENTED);
    }

    @PUT
    @Path("/applications/{name}/unbind/{service}")
    @Override
    public Response unbindApplication(@PathParam("name") String name, @PathParam("service") String service, 
            @Context HttpHeaders headers) {
        
        throw new WebApplicationException("Not supported", Status.NOT_IMPLEMENTED);
    }

    @Override
    protected Credentials extractCredentials(HttpHeaders headers) {
        throw new WebApplicationException("Not supported", Status.NOT_IMPLEMENTED);
    }
}