package eu.atos.paas.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.openshift.client.cartridge.IStandaloneCartridge;

import eu.atos.paas.data.CredentialsMap;
import eu.atos.paas.credentials.ApiUserPasswordCredentials;
import eu.atos.paas.credentials.Credentials;
import eu.atos.paas.data.Provider;
import eu.atos.paas.Module;
import eu.atos.paas.PaasSession;
import eu.atos.paas.ServiceApp;


public class OpenShiftResource extends PaasResource
{
    private static Logger log = LoggerFactory.getLogger(OpenShiftResource.class);

    
    /**
     * 
     * Constructor
     * @param client
     */
    public OpenShiftResource(Provider provider, ClientMap clientMap)
    {
        super(provider, clientMap);
    }
    
    
//    @POST
//    @Path("/applications/git")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response createApplication2(@Context HttpHeaders headers, @FormParam("appName") String appname, 
//            @FormParam("appGitUrl") String appGitUrl)
//    {
//        try
//        {
//            log.info("createApplication({})", appname);
//            
//            PaasSession session = getSession(headers);
//
//            Application result;
//            Module m = session.deploy(appname, new DeployParameters(appGitUrl, IStandaloneCartridge.NAME_JBOSSEWS));
//            result = new Application(m.getName(), m.getUrl());
//            
//            // Response
//            return generateJSONResponse(Response.Status.OK, OperationResult.OK,
//                                        "POST /applications/",
//                                        "application " + result.getName() + " created / deployed: " + result.getUrl());
//        }
//        catch (Exception e)
//        {
//            // Response
//            return generateJSONResponse(Response.Status.INTERNAL_SERVER_ERROR, OperationResult.ERROR,
//                                        "POST /applications/",
//                                        "application not created / deployed: " + e.getMessage());
//        }
//    }
    
    
    @PUT
    @Path("/applications/{name}/bind/{service}")
    @Override
    public Response bindApplication(@PathParam("name") String name, @PathParam("service") String service, @Context HttpHeaders headers)
    {
        log.info("bindApplication({}, {})", name, service);
        PaasSession session = getSession(headers);
        
        Module m = session.getModule(name);
        // openshift ... mysql-5.5
        session.bindToService(m, new ServiceApp(service));

        // Response
        return generateJSONResponse(Response.Status.OK, OperationResult.OK,
                                    "PUT /applications/" + name + "/bind/" + service, 
                                    "service " + service + " binded to app: " + name);
    }
    

    @PUT
    @Path("/applications/{name}/unbind/{service}")
    @Override
    public Response unbindApplication(@PathParam("name") String name, @PathParam("service") String service, @Context HttpHeaders headers)
    {
        log.info("unbindApplication({}, {})", name, service);
        PaasSession session = getSession(headers);
        
        Module m = session.getModule(name);
        // openshift ... mysql-5.5
        session.unbindFromService(m, new ServiceApp(service));

        // Response
        return generateJSONResponse(Response.Status.OK, OperationResult.OK,
                                    "PUT /applications/" + name + "/unbind/" + service, 
                                    "service " + service + " unbinded from app: " + name);
    }


    @Override
    protected Credentials buildCredentialsFromFieldsMap(CredentialsMap credentialsMap)
            throws IllegalArgumentException {
        
        /*
         * Set API field to provider URL in case using a specific OpenShift provider
         */
        if (!this.getProvider().getName().equals(Constants.Providers.OPENSHIFT)) {
            credentialsMap.put(ApiUserPasswordCredentials.API, provider.getUrl().toString());
        }
        return new ApiUserPasswordCredentials(credentialsMap);
    }

}
