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

import static stormy.pythian.core.utils.ReflectionHelper.setConfiguration;
import static stormy.pythian.core.utils.ReflectionHelper.setFeaturesMapper;
import static stormy.pythian.core.utils.ReflectionHelper.setInputStreams;
import static stormy.pythian.core.utils.ReflectionHelper.setProperties;
import static stormy.pythian.core.utils.ReflectionHelper.setTopology;

import java.util.Map;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.core.configuration.InputStreamConfiguration;
import stormy.pythian.core.configuration.OutputStreamConfiguration;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.FeaturesMapper;
import stormy.pythian.model.instance.FixedFeaturesMapper;
import stormy.pythian.model.instance.UserSelectionFeaturesMapper;
import backtype.storm.Config;

public class ComponentFactory {

	private TridentTopology tridentTopology;
	private Config config;

	public ComponentFactory() {
	}

	public ComponentFactory(TridentTopology tridentTopology, Config config) {
		this.tridentTopology = tridentTopology;
		this.config = config;
	}

	public Component createComponent(ComponentConfiguration configuration, Map<String, Stream> inputStreams) {
		try {
			Component component = configuration.descriptor.clazz.newInstance();
			setProperties(component, configuration.properties);
			setInputStreams(component, inputStreams);
			setTopology(component, tridentTopology);
			setConfiguration(component, config);
			setFeaturesMappers(component, configuration);

			component.init();

			return component;
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to add component " + configuration, e);
		}
	}

	private void setFeaturesMappers(Component component, ComponentConfiguration configuration) {
		for (InputStreamConfiguration isConfiguration : configuration.inputStreams) {
			FeaturesMapper mapper = createFeatureMapper(isConfiguration);
			String streamName = isConfiguration.getStreamName();
			setFeaturesMapper(component, streamName, mapper);
		}

		for (OutputStreamConfiguration osConfiguration : configuration.outputStreams) {
			FeaturesMapper mapper = new FixedFeaturesMapper(osConfiguration.mappings);
			String streamName = osConfiguration.getStreamName();
			setFeaturesMapper(component, streamName, mapper);
		}
	}

	private FeaturesMapper createFeatureMapper(InputStreamConfiguration configuration) {
		FeaturesMapper mapper = null;
		switch (configuration.getMappingType()) {
		case USER_SELECTION:
			mapper = new UserSelectionFeaturesMapper(configuration.getSelectedFeatures());
			break;
		case FIXED_FEATURES:
			mapper = new FixedFeaturesMapper(configuration.getMappings());
			break;
		}
		return mapper;
	}
}
