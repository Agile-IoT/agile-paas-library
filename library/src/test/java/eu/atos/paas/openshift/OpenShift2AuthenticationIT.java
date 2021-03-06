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
package eu.atos.paas.openshift;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import eu.atos.paas.AuthenticationException;
import eu.atos.paas.Groups;
import eu.atos.paas.PaasClient;
import eu.atos.paas.TestConfigProperties;
import eu.atos.paas.credentials.ApiUserPasswordCredentials;
import eu.atos.paas.openshift2.Openshift2Client;

@Test(groups = Groups.OPENSHIFT2)
public class OpenShift2AuthenticationIT {

    PaasClient client;
    
    public OpenShift2AuthenticationIT() {
    }

    @BeforeTest
    public void initialize()
    {
        client = new Openshift2Client();
    }
    
    @Test
    public void testRightAuthentication() {
        client.getSession(new ApiUserPasswordCredentials(
                TestConfigProperties.getInstance().getOp_api(),
                TestConfigProperties.getInstance().getOp_user(), 
                TestConfigProperties.getInstance().getOp_password()));

    }

    @Test
    public void testWrongAuthentication() {
        
        try {
            client.getSession(new ApiUserPasswordCredentials(
                    TestConfigProperties.getInstance().getOp_api(), "wrong-user", "wrong-password"));
            fail("Did not throw exception");
            
        } catch (AuthenticationException e) {
            
            assertTrue(true);
            return;
        }
    }
    
    @Test
    public void testWrongApi() {
        
        try {
            
            client.getSession(new ApiUserPasswordCredentials("", "user", "password"));
            fail("Did not throw exception");
            
        } catch (AuthenticationException e) {
            
            assertTrue(true);
        }
    }
}
