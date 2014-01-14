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
package stormy.pythian.core.topology;

import java.util.Map;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.state.StateFactory;
import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.FeaturesIndex;
import stormy.pythian.model.instance.Instance;
import backtype.storm.Config;
import backtype.storm.generated.StormTopology;

public class PythianTopology {

	private ComponentFactory componentFactory;
	private AvailableComponentPool componentPool;
	private FeaturesIndexFactory featuresIndexFactory;
	private PythianStateFactory pythianStateFactory;

	private TridentTopology tridentTopology;
	private Config config;

	public PythianTopology() {
		tridentTopology = new TridentTopology();
		config = new Config();
		config.registerSerialization(Instance.class);

		componentFactory = new ComponentFactory();
		componentFactory.init(tridentTopology, config);

		componentPool = new AvailableComponentPool();
		featuresIndexFactory = new FeaturesIndexFactory();
		pythianStateFactory = new PythianStateFactory();
	}

	public void build(PythianToplogyConfiguration topologyConfiguration) {
		componentPool.addConnections(topologyConfiguration.getConnections());
		componentPool.addComponents(topologyConfiguration.getComponents());

		featuresIndexFactory.init(topologyConfiguration);

		Map<String, StateFactory> componentStateFactories = pythianStateFactory.createStateFactories(topologyConfiguration);

		// Init components
		while (!componentPool.isEmpty()) {
			ComponentConfiguration configuration = componentPool.getAvailableComponent();
			if (configuration != null) {
				Map<String, Stream> inputStreams = componentPool.getAvailableInputStreams(configuration);
				Map<String, FeaturesIndex> inputFeaturesIndexes = featuresIndexFactory.createInputFeaturesIndexes(configuration);
				Map<String, FeaturesIndex> outputFeaturesIndexes = featuresIndexFactory.createOutputFeaturesIndexes(configuration);

				Component component = componentFactory.createComponent(configuration, componentStateFactories, inputStreams, inputFeaturesIndexes, outputFeaturesIndexes);

				componentPool.registerBuildedComponent(component, configuration);
			} else {
				throw new IllegalArgumentException("Unable to create topology, some connections are missing");
			}
		}
	}

	public StormTopology getStormTopology() {
		return tridentTopology.build();
	}

	public Config getTridentConfig() {
		return config;
	}
}
