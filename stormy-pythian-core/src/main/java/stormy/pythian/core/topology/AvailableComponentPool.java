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

import static com.google.common.collect.FluentIterable.from;
import static stormy.pythian.core.configuration.ConnectionConfiguration.connectedTo;
import static stormy.pythian.core.utils.ReflectionHelper.getOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import storm.trident.Stream;
import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.core.configuration.ConnectionConfiguration;
import stormy.pythian.model.component.Component;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class AvailableComponentPool {

    private List<ComponentConfiguration> components = new ArrayList<>();
    private List<ConnectionConfiguration> connections = new ArrayList<>();

    private Multimap<String, AvailableStream> availableStreams = HashMultimap.create();

    public AvailableComponentPool() {
    }

    public AvailableComponentPool(List<ComponentConfiguration> configurations, List<ConnectionConfiguration> connections) {
        this.connections = connections;
        this.components = new ArrayList<>(configurations);
    }

    public void addComponents(List<ComponentConfiguration> configurations) {
        this.components.addAll(configurations);
    }

    public void addConnections(List<ConnectionConfiguration> configurations) {
        this.connections.addAll(configurations);
    }

    public ComponentConfiguration getAvailableComponent() {
        return Iterables.tryFind(components, IS_AVAILABLE).orNull();
    }

    public Map<String, Stream> getAvailableInputStreams(ComponentConfiguration component) {
        Map<String, Stream> availableInputStreams = new HashMap<>();

        for (AvailableStream availableStream : availableStreams.get(component.getId())) {
            availableInputStreams.put(availableStream.name, availableStream.stream);
        }

        return availableInputStreams;
    }

    public void registerBuildedComponent(Component component, ComponentConfiguration configuration) {
        this.components.remove(configuration);

        for (ConnectionConfiguration connection : connections) {
            if (connection.isFrom(configuration)) {
                Stream stream = getOutputStream(component, connection.retrieveFromStreamName());
                availableStreams.put(connection.retrieveToComponent(), new AvailableStream(connection.retrieveToStreamName(), stream));
            }
        }
    }

    public boolean isEmpty() {
        return components.isEmpty();
    }

    private static class AvailableStream {

        public String name;
        public Stream stream;

        public AvailableStream(String name, Stream stream) {
            this.name = name;
            this.stream = stream;
        }

    }

    private static Function<AvailableStream, String> EXTRACT_NAME = new Function<AvailableStream, String>() {
        @Override
        public String apply(AvailableStream availableStream) {
            return availableStream.name;
        }
    };

    private static Function<ConnectionConfiguration, String> EXTRACT_TO_STREAM_NAME = new Function<ConnectionConfiguration, String>() {
        @Override
        public String apply(ConnectionConfiguration connetion) {
            return connetion.retrieveToStreamName();
        }
    };

    private Predicate<ComponentConfiguration> IS_AVAILABLE = new Predicate<ComponentConfiguration>() {
        @Override
        public boolean apply(ComponentConfiguration component) {
            List<String> connectedInputStreams = from(connections).filter(connectedTo(component)).transform(EXTRACT_TO_STREAM_NAME).toList();
            List<String> availableInputStreams = from(availableStreams.get(component.getId())).transform(EXTRACT_NAME).toList();
            return availableInputStreams.containsAll(connectedInputStreams);
        }
    };
}
