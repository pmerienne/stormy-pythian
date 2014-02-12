package stormy.pythian.web.jersey;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import stormy.pythian.web.resource.TopologiesResource;

public class ResourceConfiguration extends ResourceConfig {

    /**
     * Register JAX-RS application components.
     */
    public ResourceConfiguration() {
        registerClasses(RequestContextFilter.class, JacksonFeature.class, TopologiesResource.class);
    }
}
