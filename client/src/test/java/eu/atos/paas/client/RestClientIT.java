/**
 *  Copyright (c) 2017 Atos, and others
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  Contributors:
 *  Atos - initial implementation
 */
package eu.atos.paas.client;

import org.testng.annotations.Test;

import eu.atos.paas.Groups;
import eu.atos.paas.TestConstants;
import eu.atos.paas.client.RestClient.ProviderClient;
import eu.atos.paas.credentials.UserPasswordCredentials;
import eu.atos.paas.data.Application;
import eu.atos.paas.data.ApplicationToCreate;
import eu.atos.paas.data.CredentialsMap;
import eu.atos.paas.data.Provider;
import eu.atos.paas.data.ApplicationToCreate.ArtifactType;
import eu.atos.paas.resources.Constants;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.core.Response.Status;

import org.testng.annotations.BeforeClass;
import static org.testng.AssertJUnit.*;

@Test(groups = Groups.DUMMY)
public class RestClientIT {
    
    RestClient client;
    ProviderClient provider;
    CredentialsMap credentials;
    
    @BeforeClass
    public void beforeClass() throws MalformedURLException {
        
        client = new RestClient(TestConstants.SERVER_URL);
        credentials = CredentialsMap.builder()
                .item(UserPasswordCredentials.USER, "user")
                .item(UserPasswordCredentials.PASSWORD, "password")
                .build();
        provider = client.getProvider("dummy", credentials);
    }

    @Test(priority = 1)
    public void testAuthentication() {
        /*
         * Just check if authorization do not fail
         */
        provider.getApplications();
    }
    
    @Test(priority = 1)
    public void testWrongAuthentication() {
        
        CredentialsMap wrongCredentials = CredentialsMap.builder()
                .item(UserPasswordCredentials.USER, "user")
                .item(UserPasswordCredentials.PASSWORD, "wrong-password")
                .build();
        ProviderClient provider = client.getProvider("dummy", wrongCredentials);
        try {
            provider.getApplications();
            fail("Did not throw exception");
        } catch (RestClientException e) {
            assertEquals(Status.UNAUTHORIZED.getStatusCode(), e.getError().getCode());
        }
    }
    
    @Test(priority = 5)
    public void testSetWrongApiVersion() {
        
        try {
            
            ProviderClient provider = client.getProvider("dummy", "donotexists", credentials);
            provider.getApplications();
        }
        catch (RestClientException e) {
            assertEquals(Status.BAD_REQUEST.getStatusCode(), e.getError().getCode());
            return;
        }
        
    }

    @Test(priority = 5)
    public void testSetApiVersion() {
        ProviderClient provider = client.getProvider("dummy", "v1", credentials);
        provider.getApplications();
    }
    
    @Test(priority = 10)
    public void getProviderDescription() {
        Provider p = provider.getProviderDescription();
        
        assertEquals(Constants.Providers.DUMMY, p.getName());
    }
    
    @Test(priority = 20)
    public void createApplicationWithArtifact() throws IOException {
        
        String appName = "test";
        ApplicationToCreate appToCreate = new ApplicationToCreate.Builder(
                    appName, RestClient.class.getResourceAsStream("/SampleApp1.war"), ArtifactType.TARGET)
                .build();
        Application app1 = provider.createApplication(appToCreate);
        assertNotNull(app1);
        assertEquals(appName, app1.getName());
        Application app2 = provider.getApplication(appName);
        assertEquals(appName, app2.getName());
        
        assertTrue(!app1.getUrl().toString().isEmpty());
        assertEquals(app1.getUrl(), app2.getUrl());
    }
    
    @Test(priority = 21)
    public void createApplicationWithGitUrl() throws IOException {
        
        String appName = "test2";
        ApplicationToCreate appToCreate = new ApplicationToCreate.Builder(
                    appName, new URL("https://github.com/octocat/Hello-World.git"))
                .programmingLanguage("Java")
                .build();
        Application app1 = provider.createApplication(appToCreate);
        assertNotNull(app1);
        assertEquals(appName, app1.getName());
        Application app2 = provider.getApplication(appName);
        assertEquals(appName, app2.getName());
        
        assertTrue(!app1.getUrl().toString().isEmpty());
        assertEquals(app1.getUrl(), app2.getUrl());
    }

    @Test(priority = 22)
    public void createApplicationWithImageName() throws IOException {
        
        String appName = "test3";
        ApplicationToCreate appToCreate = new ApplicationToCreate.Builder(
                    appName, "centos/mongodb-26-centos7")
                .programmingLanguage("Java")
                .env("MONGODB_USER", "admin")
                .env("MONGODB_PASSWORD", "secret")
                .property("PORT", "27017")
                .build();
        Application app1 = provider.createApplication(appToCreate);
        assertNotNull(app1);
        assertEquals(appName, app1.getName());
        Application app2 = provider.getApplication(appName);
        assertEquals(appName, app2.getName());
        
        assertTrue(!app1.getUrl().toString().isEmpty());
        assertEquals(app1.getUrl(), app2.getUrl());
    }

    @Test(priority = 10)
    public void getApplicationShouldFail() {
        
        try {
            provider.getApplication("noexiste");
            /* fails below */
        }
        catch (RestClientException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getError().getCode());
            return;
        }
        fail("Should have failed with 404");
    }
}
