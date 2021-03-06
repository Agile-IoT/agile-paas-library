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
package eu.atos.paas.openshift2;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.openshift.client.IApplication;
import com.openshift.client.IGear;
import com.openshift.client.IGearGroup;
import com.openshift.client.cartridge.ICartridge;

import eu.atos.paas.PaasProviderException;


/**
 * 
 *
 * @author ATOS
 * @date 21/3/2016-14:05:37
 */
public class ModuleImpl implements eu.atos.paas.Module
{

    
    private IApplication app;
    private URL url;
    
    /**
     * 
     * Constructor
     * @param app
     */
    public ModuleImpl(IApplication appl)
    {
        this.app = appl;
        try {
            
            this.url = new URL(app.getApplicationUrl());
            
        } catch (MalformedURLException e) {
            /*
             * this should not happen
             */
            throw new PaasProviderException("Error in URL=" + app.getApplicationUrl() + " from provider ", e);
        }
    }
    
    
    @Override
    public String getName()
    {
        return this.app.getName();
    }

    
    @Override
    public URI getUrl()
    {
        try {
            
            return url.toURI();
            
        } catch (URISyntaxException e) {
            /*
             * this should not happen
             */
            throw new IllegalArgumentException("Error in URL=" + url + " from provider ", e);
        }
    }

    
    @Override
    public State getState() {
        return getRunningInstances() > 0? State.STARTED : State.STOPPED;
    }
    
    
    @Override
    public String getAppType()
    {
        return app.getCartridge().getName();
    }

    
    @Override
    public int getRunningInstances()
    {
        int runningInst = 0;
        
        Collection<IGearGroup> gearGroups = app.getGearGroups();
        for (IGearGroup group : gearGroups)
        {
            for (ICartridge cartridges : group.getCartridges())
            {
                if (cartridges.getName().equalsIgnoreCase(app.getCartridge().getName()))
                {
                    for (IGear gear : group.getGears())
                    {
                        if (gear.getState().getState().equalsIgnoreCase("STARTED"))
                        {
                            runningInst++;
                        }
                    }
                    break;
                }
            }
        }
        return runningInst;
    }

    
    @Override
    public List<String> getServices()
    {
        boolean isApp = false;
        String serv = "";
        ArrayList<String> lServices = new ArrayList<String>(3);
        
        Collection<IGearGroup> res = app.getGearGroups();
        for (IGearGroup g : res)
        {
            for (ICartridge cres2 : g.getCartridges())
            {
                
                if (cres2.getName().equalsIgnoreCase(app.getCartridge().getName()))
                {
                    for (IGear ig : g.getGears())
                    {
                        if (ig.getState().getState().equalsIgnoreCase("STARTED"))
                        {
                            isApp = true;
                        }
                    }
                    break;
                }
                else 
                {
                    serv = cres2.getName();
                }
            }
            
            if (!isApp)
            {
                lServices.add(serv);
            }
            isApp = false;
        }
        
        return lServices;
    }

    
    @Override
    public Map<String, String> getEnv()
    {
        // TODO Auto-generated method stub
        return null;
    }
    

}
