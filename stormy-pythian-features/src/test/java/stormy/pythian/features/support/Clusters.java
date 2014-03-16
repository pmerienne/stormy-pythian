package stormy.pythian.features.support;

import static stormy.pythian.features.support.Environment.BASE_API_PATH;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import cucumber.api.java.After;

public class Clusters {

	private final Multimap<String, String> deployed;

	public Clusters() {
		deployed = ArrayListMultimap.create();
	}

	public void register(String cluster, String topolog) {
		deployed.put(cluster, topolog);
	}

	@After
	public void undeployAll() {
		for (String clusterName : deployed.keySet()) {
			for (String topologyId : deployed.get(clusterName)) {
				undeploy(clusterName, topologyId);
			}
		}

		deployed.clear();
	}

	private void undeploy(String clusterName, String topologyId) {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpDelete delete = new HttpDelete(BASE_API_PATH + "clusters/" + clusterName + "/topologies/" + topologyId);
			httpClient.execute(delete);
		} catch (Exception ex) {
			// Nothing to do
		}
	}
}
