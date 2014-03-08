package stormy.pythian.features.support;

import java.util.HashMap;
import java.util.Map;
import cucumber.api.java.After;

public class Topologies {

    private final Map<String, String> topologies;

    public Topologies() {
        this.topologies = new HashMap<>();
    }

    public void storeId(String topologyName, String topologyId) {
        this.topologies.put(topologyName, topologyId);
    }

    public String getId(String topologyName) {
        return topologies.get(topologyName);
    }

    @After
    public void clean() {
        this.topologies.clear();
    }

}
