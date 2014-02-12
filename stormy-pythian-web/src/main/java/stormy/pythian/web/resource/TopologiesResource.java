/**
 * Copyright 2013-2015 Pierre Merienne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stormy.pythian.web.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
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
