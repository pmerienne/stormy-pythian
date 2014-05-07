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
import stormy.pythian.model.instance.ListedFeaturesMapper;
import stormy.pythian.model.instance.NamedFeaturesMapper;
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

    public Component createComponent(ComponentConfiguration configuration, Map<String, StateFactory> stateFactories, Map<String, Stream> inputStreams) {
        try {
            Component component = configuration.retrieveImplementationClass().newInstance();
            setProperties(component, configuration.getProperties());
            setInputStreams(component, inputStreams);
            setTopology(component, tridentTopology);
            setConfiguration(component, config);
            setFeaturesMappers(component, configuration);
            setStateFactories(component, configuration, stateFactories);

            component.init();

            return component;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to add component " + configuration.retrieveName(), e);
        }
    }

    private void setStateFactories(Component component, ComponentConfiguration componentConfiguration, Map<String, StateFactory> stateFactories) {
        for (String stateName : stateFactories.keySet()) {
            setStateFactory(component, stateName, stateFactories.get(stateName));
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
    private void setFeaturesMappers(Component component, ComponentConfiguration configuration) {
        for (InputStreamConfiguration isConfiguration : configuration.getInputStreams()) {
            String streamName = isConfiguration.retrieveStreamName();

            switch (isConfiguration.retrieveMappingType()) {
                case LISTED:
                    setFeaturesMapper(component, streamName, new ListedFeaturesMapper(isConfiguration.getSelectedFeatures()));
                    break;
                case NAMED:
                    setFeaturesMapper(component, streamName, new NamedFeaturesMapper(isConfiguration.getMappings()));
                    break;
                default:
                    break;
            }
        }

        for (OutputStreamConfiguration osConfiguration : configuration.getOutputStreams()) {
            String streamName = osConfiguration.retrieveStreamName();

            switch (osConfiguration.retrieveMappingType()) {
                case LISTED:
                    setFeaturesMapper(component, streamName, new ListedFeaturesMapper(osConfiguration.retrieveNewFeatures()));
                    break;
                case NAMED:
                    setFeaturesMapper(component, streamName, new NamedFeaturesMapper(osConfiguration.getMappings()));
                    break;
                default:
                    break;
            }
        }
    }
}
