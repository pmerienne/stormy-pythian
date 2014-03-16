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
package stormy.pythian.service.cluster;

import static stormy.pythian.service.cluster.TopologyState.Status.fromStormStatus;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.core.topology.PythianTopology;
import stormy.pythian.service.topology.TopologyRepository;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.generated.TopologySummary;

@Service
public class LocalCLusterService {

	@Autowired
	private TopologyRepository topologyRepository;

	private LocalCluster cluster;

	@PostConstruct
	protected void initLocalCluster() {
		cluster = new LocalCluster();
	}

	@PreDestroy
	protected void shutdownLocalCluster() {
		cluster.shutdown();
	}

	public void launch(String topologyId) throws TopologyLaunchException {
		PythianToplogyConfiguration configuration = topologyRepository.findById(topologyId);
		if (configuration == null) {
			throw new TopologyLaunchException("No topology found with id " + topologyId);
		}

		StormTopology stormTopology;
		Config tridentConfig;
		try {
			PythianTopology pythianTopology = new PythianTopology();
			pythianTopology.build(configuration);
			stormTopology = pythianTopology.getStormTopology();
			tridentConfig = pythianTopology.getTridentConfig();
		} catch (Exception ex) {
			throw new TopologyLaunchException("Topology could not be builded", ex);
		}

		try {
			cluster.submitTopology(topologyId, tridentConfig, stormTopology);
		} catch (Exception ex) {
			throw new TopologyLaunchException("Topology could not be launched", ex);
		}
	}

	public void kill(String topologyId) throws TopologyKillException {
		try {
			cluster.killTopology(topologyId);
		} catch (Exception ex) {
			throw new TopologyKillException("Topology could not be killed", ex);
		}
	}

	public List<TopologyState> getTopologyStates() {
		List<TopologySummary> summaries = cluster.getClusterInfo().get_topologies();
		List<TopologyState> states = new ArrayList<>(summaries.size());

		for (TopologySummary summary : summaries) {
			PythianToplogyConfiguration topology = topologyRepository.findById(summary.get_name());
			if (topology != null) {
				states.add(new TopologyState(topology.getId(), topology.getName(), fromStormStatus(summary.get_status())));
			}
		}

		return states;
	}

	public TopologyState getTopologyState(String topologyId) {
		PythianToplogyConfiguration configuration = topologyRepository.findById(topologyId);
		List<TopologySummary> summaries = cluster.getClusterInfo().get_topologies();

		for (TopologySummary summary : summaries) {
			if (StringUtils.equals(summary.get_name(), topologyId)) {
				return new TopologyState(configuration.getId(), configuration.getName(), fromStormStatus(summary.get_status()));
			}
		}

		return new TopologyState(configuration.getId(), configuration.getName(), TopologyState.Status.UNDEPLOYED);
	}
}
