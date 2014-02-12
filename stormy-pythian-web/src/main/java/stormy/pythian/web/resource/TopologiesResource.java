package stormy.pythian.web.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.service.topology.TopologyService;

@Component
@Path("topologies")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class TopologiesResource {

    @Autowired
    private TopologyService topologyService;

    @GET
    @Path("/{topology-id}")
    public PythianToplogyConfiguration getTopology(@PathParam("topology-id") String topologyId) {
        return topologyService.findById(topologyId);
    }

    @DELETE
    @Path("/{topology-id}")
    public void delete(@PathParam("topology-id") String topologyId) {
        topologyService.delete(topologyId);
    }

    @PUT
    public PythianToplogyConfiguration save(PythianToplogyConfiguration topology) {
        return topologyService.save(topology);
    }

    @GET
    public Collection<PythianToplogyConfiguration> findAll() {
        return topologyService.findAll();
    }
}
