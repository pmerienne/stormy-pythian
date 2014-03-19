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

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import stormy.pythian.service.cluster.LocalCLusterService;
import stormy.pythian.service.cluster.TopologyKillException;
import stormy.pythian.service.cluster.TopologyLaunchException;
import stormy.pythian.service.cluster.TopologyState;

import com.google.common.base.Preconditions;

@Component
@Path("clusters")
public class ClustersResource {

	private final static String LOCAL_CLUSTER_NAME = "local";

	@Autowired
	private LocalCLusterService localCLusterService;

	@GET
	@Path("/{cluster-name}/topologies")
	@Produces(APPLICATION_JSON)
	public List<TopologyState> retieveTopologyStates(@PathParam("cluster-name") String clusterName) {
		if (LOCAL_CLUSTER_NAME.equals(clusterName)) {
			List<TopologyState> states = localCLusterService.getTopologyStates();
			return states;
		} else {
			throw new IllegalArgumentException("No cluster named " + clusterName + " found");
		}
	}

	@GET
	@Path("/{cluster-name}/topologies/{topology-id}")
	public TopologyState retieveTopologyState(@PathParam("cluster-name") String clusterName, @PathParam("topology-id") String topologyId) {
		if (LOCAL_CLUSTER_NAME.equals(clusterName)) {
			Preconditions.checkNotNull(topologyId, "Topology id is mandatory");
			TopologyState topologyState = localCLusterService.getTopologyState(topologyId);
			return topologyState;
		} else {
			throw new IllegalArgumentException("No cluster named " + clusterName + " found");
		}
	}

	@POST
	@Path("/{cluster-name}/topologies/{topology-id}")
	public void deployTopology(@PathParam("cluster-name") String clusterName, @PathParam("topology-id") String topologyId) throws TopologyLaunchException {
		if (LOCAL_CLUSTER_NAME.equals(clusterName)) {
			Preconditions.checkNotNull(topologyId, "Topology id is mandatory");
			localCLusterService.launch(topologyId);
		} else {
			throw new IllegalArgumentException("No cluster named " + clusterName + " found");
		}
	}

	@DELETE
	@Path("/{cluster-name}/topologies/{topology-id}")
	public void killTopology(@PathParam("cluster-name") String clusterName, @PathParam("topology-id") String topologyId) throws TopologyKillException {
		if (LOCAL_CLUSTER_NAME.equals(clusterName)) {
			Preconditions.checkNotNull(topologyId, "Topology id is mandatory");
			localCLusterService.kill(topologyId);
		} else {
			throw new IllegalArgumentException("No cluster named " + clusterName + " found");
		}
	}

}
