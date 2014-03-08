package stormy.pythian.features.support;

import java.util.HashMap;
import java.util.Map;

public class Topologies {

	private Map<String, String> topologies = new HashMap<>();

	public void storeId(String topologyName, String topologyId) {

	}

	public String getId(String topologyName) {
		return topologies.get(topologyName);
	}
}
