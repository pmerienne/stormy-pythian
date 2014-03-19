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
