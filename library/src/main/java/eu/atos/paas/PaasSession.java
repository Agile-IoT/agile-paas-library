package eu.atos.paas;

/**
 * Defines the operations to be implemented by a provider session.
 * 
 * This is the exception handling to be used by each implementation:
 * <li>PaasException encapsulates exceptions raised by provider clients or other expected errors (authentication error,
 *     create an application with an existing name, delete a non existing application...)
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
        String getBuildpackUrl();
        String getCartridge();
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
     * Creates and deploy an application.
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
     * @throws NotFoundException
     * @throws PaasProviderException
     */
    void startStop(Module module, PaasSession.StartStopCommand command) 
            throws NotFoundException, PaasProviderException;
    
    
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
     * @throws PaasProviderException on unexpected exception from provider.
     */
    Module getModule(String moduleName) throws PaasException;

    
}