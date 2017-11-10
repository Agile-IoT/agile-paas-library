/**
 * Copyright 2016 Atos
 * Contact: Atos <roman.sosa@atos.net>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.atos.paas;

import java.net.URL;

/**
 * Defines the operations to be implemented by a provider session.
 * 
 * This is the exception handling to be used by each implementation:
 * <li>{@link PaasException} encapsulates exceptions raised by provider clients or other expected errors (authentication error,
 *     create an application with an existing name, delete a non existing application...)
 *     A special case of PaasException is {@link PaasProviderException}, which encloses an unexpected exception 
 *     thrown by the provider.
 * <li>Normal runtime exceptions that indicate a bug. For example, NullPointerException or 
 *     IllegalArgumentException in case of wrong 
 *     parameters (they can occur because of a bug in the library implementation or a bug 
 *     in the library client; in both cases, they are a bug).
 * <li>NotImplementedException. This exception should be thrown when a specific functionality is not implemented yet,
 *     but it is intended to be implemented in the future.
 */
public interface PaasSession {
    
    
    public interface DeployParameters {
        String getPath();
        URL getGitUrl();
        String getProperty(String propertyName);
        
        public static class Properties {
            public static final String CARTRIDGE = "cartridge";
            public static final String BUILDPACK_URL = "buildpack_url";
        }

    }

    
    public enum StartStopCommand {
        START,
        STOP
    }
    
    
    public enum ScaleUpDownCommand {
        SCALE_UP_INSTANCES,
        SCALE_DOWN_INSTANCES,
        SCALE_UP_MEMORY,
        SCALE_DOWN_MEMORY
    }
    
    
    public enum ScaleCommand {
        SCALE_INSTANCES,
        SCALE_MEMORY,
        SCALE_DISK
    }

    /**
     * Creates an application.
     * 
     * The application must be later deployed using createApplication. 
     * The status of the application is State.UNDEPLOYED or State.STARTED.
     * 
     * @param moduleName Name of module to deploy
     * @param params Parameters of deployment (buildpack, cartridge, artifact or git url..., number of instances)
     * @return Current model of application
     * @throws AlreadyExistsException if an application named <code>moduleName</code> is already created.
     * @throws PaasProviderException on unexpected error from provider.
     */
    Module createApplication(String moduleName, PaasSession.DeployParameters params)
        throws PaasProviderException, AlreadyExistsException;
    

    /**
     * Updates the application code or env vars of a created application.
     * 
     * If the application is in state UNDEPLOYED and the code is supplied, the state is STARTED.
     * 
     * @param moduleName Name of module to deploy
     * @param params Parameters to be changed
     * @return Current model of application
     * @throws NotFoundException if the application has not been created.
     * @throws PaasProviderException on unexpected error from provider.
     */
    Module updateApplication(String moduleName, PaasSession.DeployParameters params)
        throws NotFoundException, PaasProviderException;
    
    /**
     * Creates and deploy an application. It is simply a facade over createApplication and updateApplication.
     * 
     * @param moduleName Name of module to deploy
     * @param params Parameters of deployment (buildpack, cartridge, artifact or git url..., number of instances)
     * @return Model of created application, including deployment url.
     * @throws AlreadyExistsException if an application named <code>moduleName</code> is already created.
     * @throws PaasProviderException on unexpected error from provider.
     */
    Module deploy(String moduleName, PaasSession.DeployParameters params) 
            throws PaasProviderException, AlreadyExistsException;
    
    
    /**
     * Undeploys and removes an application.
     * 
     * @param moduleName Name of module to remove
     * @throws NotFoundException if an application named <code>moduleName</code> do not exist.
     * @throws PaasProviderException on unexpected error from provider.
     */
    void undeploy(String moduleName) throws NotFoundException, PaasProviderException;
    
    
    /**
     * Starts or stops an application.
     * 
     * @param module Name of module to start/stop.
     * @param command Start/Stop command
     * @throws NotFoundException if the application is not created
     * @throws NotDeployedException if the application is not deployed, only created
     * @throws PaasProviderException on unexpected error from provider
     */
    void startStop(Module module, PaasSession.StartStopCommand command) 
            throws NotFoundException, NotDeployedException, PaasProviderException;
    
    
    /**
     * scale instances / memory of applications
     * 1. SCALE_UP_INSTANCES adds one more instance to app
     * 2. SCALE_DOWN_INSTANCES removes one instance if running instances > 1
     * 3. SCALE_UP_MEMORY
     * 4. SCALE_DOWN_MEMORY
     * 
     * @param module
     * @param command
     * @throws PaasException
     */
    void scaleUpDown(Module module, PaasSession.ScaleUpDownCommand command) throws PaasException, UnsupportedOperationException;
    
    
    /**
     * scale instances / memory of applications
     * 1. SCALE_INSTANCES sets the number of instances to 'scale_value'
     * 2. SCALE_MEMORY sets the RAM of app to 'scale_value' (in MB)
     * 3. SCALE_DISK sets the disk space value to 'scale_value' (in MB)
     * 
     * @param module
     * @param command
     * @param scale_value
     * @throws PaasException
     */
    void scale(Module module, PaasSession.ScaleCommand command, int scale_value) throws PaasException, UnsupportedOperationException;
    
    
    /**
     * 
     * @param module
     * @param service
     * @throws PaasException
     */
    void bindToService(Module module, ServiceApp service) throws PaasException;
    
    
    /**
     * 
     * @param module
     * @param service
     * @throws PaasException
     */
    void unbindFromService(Module module, ServiceApp service) throws PaasException;
    
    
    /**
     * Get the status of a module.
     * 
     * @param moduleName
     * @return Module if application with name <code>moduleName</code> exists; <code>null</code> otherwise.
     * @throws PaasProviderException on unexpected exception from provider; ForbiddenException if no permissions
     * to access the module (e.g. in Heroku).
     */
    Module getModule(String moduleName) throws PaasException;

    
}