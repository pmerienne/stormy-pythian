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
package stormy.pythian.core.configuration;

import static com.google.common.collect.Iterables.tryFind;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import stormy.pythian.core.description.ComponentDescription;
import stormy.pythian.model.component.Component;
import com.google.common.base.Predicate;

public class ComponentConfiguration {

    private String id;

    private ComponentDescription description;

    private List<PropertyConfiguration> properties = new ArrayList<>();
    private List<InputStreamConfiguration> inputStreams = new ArrayList<>();
    private List<OutputStreamConfiguration> outputStreams = new ArrayList<>();
    private Map<String, String> stateFactories = new HashMap<>();

    public ComponentConfiguration() {
    }

    public ComponentConfiguration(String id) {
        this.id = id;
    }

    public ComponentConfiguration(String id, ComponentDescription description) {
        this.id = id;
        this.description = description;
    }

    public ComponentConfiguration(String id, ComponentDescription description, List<PropertyConfiguration> properties, List<InputStreamConfiguration> inputStreams,
            List<OutputStreamConfiguration> outputStreams) {
        this.id = id;
        this.description = description;
        this.properties = properties;
        this.inputStreams = inputStreams;
        this.outputStreams = outputStreams;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return description.getName();
    }

    public List<InputStreamConfiguration> getInputStreams() {
        return inputStreams;
    }

    public void setInputStreams(List<InputStreamConfiguration> inputStreams) {
        this.inputStreams = inputStreams;
    }

    public List<OutputStreamConfiguration> getOutputStreams() {
        return outputStreams;
    }
    
    public List<PropertyConfiguration> getProperties() {
        return properties;
    }

    public void setOutputStreams(List<OutputStreamConfiguration> outputStreams) {
        this.outputStreams = outputStreams;
    }

    public void addStateFactory(String name, String id) {
        this.stateFactories.put(name, id);
    }

    public void addStateFactory(String name, PythianStateConfiguration configuration) {
        this.stateFactories.put(name, configuration.getId());
    }

    public void add(PropertyConfiguration property) {
        this.properties.add(property);
    }

    public void add(InputStreamConfiguration inputStream) {
        this.inputStreams.add(inputStream);
    }

    public void add(OutputStreamConfiguration outputStream) {
        this.outputStreams.add(outputStream);
    }
    
    public Map<String, String> getStateFactories() {
        return stateFactories;
    }

    public OutputStreamConfiguration findOutputStreamByName(final String name) {
        return tryFind(outputStreams, new Predicate<OutputStreamConfiguration>() {
            public boolean apply(OutputStreamConfiguration candidate) {
                return name.equals(candidate.getStreamName());
            }
        }).orNull();
    }

    public Class<? extends Component> getImplementationClass() {
        return description.getClazz();
    }


}
