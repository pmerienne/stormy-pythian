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
package stormy.pythian.service.topology;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.core.topology.PythianTopology;
import backtype.storm.LocalCluster;

@Service
public class TopologyLocalLauncherService {

	@Autowired
	private TopologyService topologyService;

	private LocalCluster cluster;

	@PostConstruct
	protected void initLocalCluster() {
		cluster = new LocalCluster();
	}

	@PreDestroy
	protected void shutdownLocalCluster() {
		cluster.shutdown();
	}

	public void launch(String topologyId) {
		cluster.killTopology(topologyId);

		PythianToplogyConfiguration configuration = topologyService.findById(topologyId);

		PythianTopology pythianTopology = new PythianTopology();
		pythianTopology.build(configuration);

		cluster.submitTopology(topologyId, pythianTopology.getTridentConfig(), pythianTopology.getStormTopology());
	}

	public void kill(String topologyId) {
		cluster.killTopology(topologyId);
	}

}
