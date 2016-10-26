package eu.atos.paas.serviceloader;

import java.util.ServiceLoader;
import java.util.Set;

import eu.atos.paas.resources.PaaSResource;

/**
 * Defines an interface to allow pluggable PaasResources using {@link ServiceLoader}.
 * 
 * An implementation of ResourceSet returns a set of ResourceDescriptor. 
 * A ResourceDescriptor links a PaasResource with the route to the resource.
 */
public interface ResourceSet {

    Set<ResourceDescriptor> getResources();
    
    public static class ResourceDescriptor {
        private String path;
        private PaaSResource resource;
        
        public ResourceDescriptor(String path, PaaSResource resource) {
            this.path = path;
            this.resource = resource;
        }
        public String getPath() {
            return path;
        }
        public PaaSResource getResource() {
            return resource;
        }
    }
}