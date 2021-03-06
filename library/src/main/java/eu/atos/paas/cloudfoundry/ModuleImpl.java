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
package eu.atos.paas.cloudfoundry;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudApplication.AppState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.atos.paas.Module;


/**
 * Cloud Foundry module
 * @author 
 *
 */
public class ModuleImpl implements Module {
    private static final Logger logger = LoggerFactory.getLogger(ModuleImpl.class);
    
    private static Map<CloudApplication.AppState, Module.State> stateMap;
    static {
        stateMap = new HashMap<>();
        stateMap.put(AppState.STARTED, State.STARTED);
        stateMap.put(AppState.STOPPED, State.STOPPED);
        stateMap.put(AppState.UPDATING, State.UPDATING);
    }
    
    private CloudApplication app;
    private List<String> lServices;
    private Map<String, String> mEnv;
    private URI url;
    private State state;
    
    
    public ModuleImpl(CloudApplication app) {
        this(app, app.getEnvAsMap());
    }
    
    /**
     * 
     * Constructor
     * @param app
     * @param m
     */
    public ModuleImpl(CloudApplication app, Map<String, ? extends Object> m) {
        this.app = app;
        this.lServices = app.getServices();
        this.mEnv = calcEnv(m);
        this.state = calcState(app);
        try {
            /*
             * app.getUris() do not return URIs!! Prefix "http://" if no protocol found.
             */
            String uri = app.getUris().get(0);
            if (!uri.contains("://")) {
                uri = String.format("http://%s", uri);
            }
            this.url = new URI(uri);
        } catch (URISyntaxException e) {
            
            /*
             * this should not happen
             */
            throw new IllegalArgumentException("Error in URL=" + app.getUris().get(0) + " from provider ", e);
        }
    }

    private Map<String, String> calcEnv(Map<String, ? extends Object> m) {
        Map<String, String> result = new HashMap<String, String>();
        for (Entry<String, ? extends Object> item : m.entrySet()) {
            if (item.getValue() instanceof String) {
                result.put(item.getKey(), String.class.cast(item.getValue()));
            }
            else {
                logger.warn(String.format("Found non string in env item [app=%s envkey=%s envvalue=%s]",
                        app.getName(), item.getKey(), item.getValue()));
            }
        }
        return result;
    }


    private State calcState(CloudApplication app) {
        State result = null;
        
        if (!app.getEnvAsMap().containsKey(CloudFoundrySession.DEPLOYED_FLAG)) {
            result = State.UNDEPLOYED;
        }
        else {
            result = stateMap.containsKey(app.getState())?
                stateMap.get(app.getState()) : State.UNKNOWN;
        }
        return result;
    }
    
    
    @Override
    public String getName()
    {
        return app.getName();
    }

    
    @Override
    public URI getUrl()
    {
        return url;
    }
    
    
    @Override
    public State getState() {
        return state;
    }
    
    
    @Override
    public String getAppType()
    {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public int getRunningInstances()
    {
        return app.getRunningInstances();
    }
    
    
    @Override
    public List<String> getServices()
    {
        return lServices;
    }
    
    
    @Override
    public Map<String, String> getEnv()
    {
        return mEnv;
    }
    

}
