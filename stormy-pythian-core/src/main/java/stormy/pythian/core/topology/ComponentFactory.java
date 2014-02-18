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
import static stormy.pythian.core.utils.ReflectionHelper.setProperties;
import static stormy.pythian.core.utils.ReflectionHelper.setStateFactory;
import static stormy.pythian.core.utils.ReflectionHelper.setTopology;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.state.StateFactory;
import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.core.configuration.InputStreamConfiguration;
import stormy.pythian.core.configuration.OutputStreamConfiguration;
import stormy.pythian.core.utils.ReflectionHelper;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.FeaturesIndex;
import stormy.pythian.model.instance.InputFixedFeaturesMapper;
import stormy.pythian.model.instance.InputUserSelectionFeaturesMapper;
import stormy.pythian.model.instance.OutputFixedFeaturesMapper;
import stormy.pythian.model.instance.OutputUserSelectionFeaturesMapper;
import backtype.storm.Config;

public class ComponentFactory {

	private TridentTopology tridentTopology;
	private Config config;

	public ComponentFactory() {
	}

	public void init(TridentTopology tridentTopology, Config config) {
		this.tridentTopology = tridentTopology;
		this.config = config;
	}

	public Component createComponent(ComponentConfiguration configuration, Map<String, StateFactory> topologyStateFactories, Map<String, Stream> inputStreams,
			Map<String, FeaturesIndex> inputFeaturesIndexes, Map<String, FeaturesIndex> outputFeaturesIndexes) {
		try {
			Component component = configuration.getImplementationClass().newInstance();
			setProperties(component, configuration.getProperties());
			setInputStreams(component, inputStreams);
			setTopology(component, tridentTopology);
			setConfiguration(component, config);
			setFeaturesMappers(component, configuration, inputFeaturesIndexes, outputFeaturesIndexes);
			setStateFactories(component, configuration, topologyStateFactories);

			component.init();

			return component;
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to add component " + configuration.getName(), e);
		}
	}

	private void setStateFactories(Component component, ComponentConfiguration componentConfiguration, Map<String, StateFactory> topologyStateFactories) {
		for (Entry<String, String> entry : componentConfiguration.getStateFactories().entrySet()) {
			String stateFactoryName = entry.getKey();
			String stateFactoryId = entry.getValue();
			StateFactory stateFactory = topologyStateFactories.get(stateFactoryId);

			setStateFactory(component, stateFactoryName, stateFactory);
		}
	}

	private void setInputStreams(Component component, Map<String, Stream> inputStreams) {
		Map<String, Stream> switchedStreams = new HashMap<>(inputStreams.size());

		for (Entry<String, Stream> stream : inputStreams.entrySet()) {
			switchedStreams.put(stream.getKey(), replaceInstanceField(stream.getValue()));
		}

		ReflectionHelper.setInputStreams(component, switchedStreams);
	}

	private Stream replaceInstanceField(Stream stream) {
		return stream.applyAssembly(new ReplaceInstanceField());
	}

	// TODO : refactor !
	private void setFeaturesMappers(Component component, ComponentConfiguration configuration, Map<String, FeaturesIndex> inputFeaturesIndexes, Map<String, FeaturesIndex> outputFeaturesIndexes) {
		for (InputStreamConfiguration isConfiguration : configuration.getInputStreams()) {
			String streamName = isConfiguration.getStreamName();

			switch (isConfiguration.getMappingType()) {
			case USER_SELECTION:
				setFeaturesMapper(component, streamName, new InputUserSelectionFeaturesMapper(inputFeaturesIndexes.get(streamName), isConfiguration.getSelectedFeatures()));
				break;
			case FIXED_FEATURES:
				setFeaturesMapper(component, streamName, new InputFixedFeaturesMapper(inputFeaturesIndexes.get(streamName), isConfiguration.getMappings()));
				break;
			default:
				throw new IllegalStateException("Unsupported mapping type : " + isConfiguration.getMappingType());
			}
		}

		for (OutputStreamConfiguration osConfiguration : configuration.getOutputStreams()) {
			String streamName = osConfiguration.getStreamName();

			switch (osConfiguration.getMappingType()) {
			case USER_SELECTION:
				OutputUserSelectionFeaturesMapper outputUserSelectionFeaturesMapper = new OutputUserSelectionFeaturesMapper(outputFeaturesIndexes.get(streamName), osConfiguration.getNewFeatures());
				setFeaturesMapper(component, streamName, outputUserSelectionFeaturesMapper);
				break;
			case FIXED_FEATURES:
				OutputFixedFeaturesMapper outputFixedFeaturesMapper = new OutputFixedFeaturesMapper(outputFeaturesIndexes.get(streamName), osConfiguration.getMappings());
				setFeaturesMapper(component, streamName, outputFixedFeaturesMapper);
				break;
			default:
				throw new IllegalStateException("Unsupported mapping type : " + osConfiguration.getMappingType());
			}
		}
	}
}
