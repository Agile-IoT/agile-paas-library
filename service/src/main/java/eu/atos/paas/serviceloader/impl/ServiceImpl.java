package eu.atos.paas.serviceloader.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import eu.atos.paas.cloudfoundry.CloudFoundryClient;
import eu.atos.paas.data.Provider;
import eu.atos.paas.heroku.HerokuClient;
import eu.atos.paas.openshift2.Openshift2Client;
import eu.atos.paas.openshift3.Openshift3Client;
import eu.atos.paas.resources.CFBasedResource;
import eu.atos.paas.resources.ClientMap;
import eu.atos.paas.resources.HerokuResource;
import eu.atos.paas.resources.OpenShiftResource;
import eu.atos.paas.serviceloader.ResourceSet;
import static eu.atos.paas.resources.Constants.Providers;

public class ServiceImpl implements ResourceSet {

    private Set<ResourceDescriptor> set;
    
    public ServiceImpl() {
        set = new HashSet<>();
        
        final HerokuResource heroku = new HerokuResource(
                new Provider(Providers.HEROKU, "https://api.heroku.com/", HerokuClient.VERSION),
                ClientMap.builder()
                    .client(new HerokuClient())
                    .build()
        );
        
        final CFBasedResource cloudfoundry = newCFBasedResource(
                new Provider(Providers.CLOUDFOUNDRY, "https://www.example.com", CloudFoundryClient.VERSION)
        );
        
        final CFBasedResource pivotal = newCFBasedResource(
                new Provider(Providers.PIVOTAL, "https://api.run.pivotal.io", CloudFoundryClient.VERSION)
        );

        final CFBasedResource bluemix = newCFBasedResource(
                new Provider(Providers.BLUEMIX, "https://api.ng.bluemix.net", CloudFoundryClient.VERSION)
        );
        
        final OpenShiftResource openshift = newOpenShiftResource(
                new Provider(
                        Providers.OPENSHIFT, 
                        "http://www.example.com",
                        new String[] { Openshift2Client.VERSION, Openshift3Client.VERSION },
                        Openshift2Client.VERSION
                )
        );
        
        final OpenShiftResource openshiftOnline = newOpenShiftResource(
                new Provider(
                        Providers.OPENSHIFT_ONLINE, 
                        "http://api.openshift.com",
                        new String[] { Openshift2Client.VERSION, Openshift3Client.VERSION },
                        Openshift2Client.VERSION
                )
        );
        
        set.add(new ResourceDescriptor("heroku", heroku));
        set.add(new ResourceDescriptor("cloudfoundry", cloudfoundry));
        set.add(new ResourceDescriptor("pivotal", pivotal));
        set.add(new ResourceDescriptor("bluemix", bluemix));
        set.add(new ResourceDescriptor("openshift", openshift));
        set.add(new ResourceDescriptor("openshift.com", openshiftOnline));
    }

    @Override
    public Set<ResourceDescriptor> getResources() {
        return Collections.unmodifiableSet(set);
    }
                
    private OpenShiftResource newOpenShiftResource(Provider provider) {
        return new OpenShiftResource(
                provider,
                ClientMap.builder()
                    .client(new Openshift2Client())
                    .client(new Openshift3Client())
                    .build()
        );
    }

    private CFBasedResource newCFBasedResource(Provider provider) {
        return new CFBasedResource(
                provider,
                ClientMap.builder()
                    .client(new CloudFoundryClient())
                    .build()
        );
    }
}
