package stormy.pythian.service.topology;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.core.description.ComponentDescriptionFactory;
import stormy.pythian.core.topology.PythianTopology;
import backtype.storm.LocalCluster;

@Service
public class TopologyLocalLauncherService {

	@Autowired
	private TopologyService topologyService;

	@Autowired
	private ComponentDescriptionFactory descriptionFactory;

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
