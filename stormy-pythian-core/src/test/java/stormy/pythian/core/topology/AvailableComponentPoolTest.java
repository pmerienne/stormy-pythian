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

import static java.util.Arrays.asList;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.mockito.Mockito.mock;
import static stormy.pythian.core.description.InputStreamDescriptionTestBuilder.inputStreamDescription;
import static stormy.pythian.core.description.OutputStreamDescriptionTestBuilder.outputStreamDescription;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import storm.trident.Stream;
import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.core.configuration.ConnectionConfiguration;
import stormy.pythian.core.description.ComponentDescription;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.component.Component;

@SuppressWarnings("serial")
public class AvailableComponentPoolTest {

    @Test
    public void should_retrieve_available_component() {
        // Given
        ComponentConfiguration configuration = createComponentConfiguration();
        List<ComponentConfiguration> configurations = asList(configuration);

        AvailableComponentPool pool = new AvailableComponentPool(configurations, new ArrayList<ConnectionConfiguration>());

        // When
        ComponentConfiguration actualComponent = pool.getAvailableComponent();

        // Then
        assertThat(actualComponent).isEqualTo(configuration);
    }

    @Test
    public void should_know_when_empty() {
        // Given
        List<ComponentConfiguration> configurations = asList(createComponentConfiguration());

        AvailableComponentPool pool = new AvailableComponentPool(configurations, new ArrayList<ConnectionConfiguration>());

        // When
        ComponentConfiguration actualConfiguration = pool.getAvailableComponent();

        assertThat(pool.isEmpty()).isFalse();
        Component component = mock(Component.class);
        pool.registerBuildedComponent(component, actualConfiguration);

        // Then
        assertThat(pool.isEmpty()).isTrue();
    }

    @Test
    public void should_not_retrieve_not_available_component() {
        // Given
        ComponentConfiguration configuration = createComponentConfigurationWithInputStreams();
        ConnectionConfiguration connection1 = new ConnectionConfiguration("", "", configuration.getId(), "in1");
        ConnectionConfiguration connection2 = new ConnectionConfiguration("", "", configuration.getId(), "in2");
        AvailableComponentPool pool = new AvailableComponentPool(asList(configuration), asList(connection1, connection2));

        // When
        ComponentConfiguration actualComponent = pool.getAvailableComponent();

        // Then
        assertThat(actualComponent).isNull();
    }

    @Test
    public void should_register_builded_component() {
        // Given
        ComponentConfiguration componentWithOutputStream = createComponentConfigurationWithOutputStream();
        ComponentConfiguration componentWithInputStreams = createComponentConfigurationWithInputStreams();

        List<ComponentConfiguration> configurations = Arrays.asList(componentWithInputStreams, componentWithOutputStream);

        List<ConnectionConfiguration> connections = new ArrayList<ConnectionConfiguration>();
        connections.add(new ConnectionConfiguration(componentWithOutputStream.getId(), "out", componentWithInputStreams.getId(), "in1"));
        connections.add(new ConnectionConfiguration(componentWithOutputStream.getId(), "out", componentWithInputStreams.getId(), "in2"));

        AvailableComponentPool pool = new AvailableComponentPool(configurations, connections);

        TestComponentWithOuputStream component1 = new TestComponentWithOuputStream();
        component1.out = mock(Stream.class);

        TestComponentWithInputStreams component2 = new TestComponentWithInputStreams();
        component2.in1 = mock(Stream.class);
        component2.in2 = mock(Stream.class);

        // When
        ComponentConfiguration actualConfiguration1 = pool.getAvailableComponent();
        pool.registerBuildedComponent(component1, actualConfiguration1);

        ComponentConfiguration actualConfiguration2 = pool.getAvailableComponent();
        pool.registerBuildedComponent(component2, actualConfiguration2);

        // Then
        assertThat(actualConfiguration1).isEqualTo(componentWithOutputStream);
        assertThat(actualConfiguration2).isEqualTo(componentWithInputStreams);
        assertThat(pool.isEmpty()).isTrue();
    }

    @Test
    public void should_find_available_input_streams() {
        // Given
        ComponentConfiguration streamSourceConfiguration = createComponentConfigurationWithOutputStream();
        TestComponentWithOuputStream streamSource = new TestComponentWithOuputStream();
        streamSource.out = mock(Stream.class);

        ComponentConfiguration componentConfiguration = createComponentConfigurationWithInputStreams();
        List<ComponentConfiguration> configurations = asList(componentConfiguration);

        List<ConnectionConfiguration> connections = new ArrayList<ConnectionConfiguration>();
        connections.add(new ConnectionConfiguration(streamSourceConfiguration.getId(), "out", componentConfiguration.getId(), "in1"));
        connections.add(new ConnectionConfiguration(streamSourceConfiguration.getId(), "out", componentConfiguration.getId(), "in2"));

        AvailableComponentPool pool = new AvailableComponentPool(configurations, connections);
        pool.registerBuildedComponent(streamSource, streamSourceConfiguration);

        // When
        Map<String, Stream> actualInputStreams = pool.getAvailableInputStreams(componentConfiguration);

        // Then
        assertThat(actualInputStreams).includes(entry("in1", streamSource.out), entry("in2", streamSource.out));
    }

    private ComponentConfiguration createComponentConfigurationWithInputStreams() {
        ComponentDescription descriptor = new ComponentDescription(TestComponentWithInputStreams.class);
        descriptor.add(inputStreamDescription().name("in1").build());
        descriptor.add(inputStreamDescription().name("in2").build());

        return new ComponentConfiguration(randomAlphabetic(6), descriptor);
    }

    private ComponentConfiguration createComponentConfigurationWithOutputStream() {
        ComponentDescription descriptor = new ComponentDescription(TestComponentWithOuputStream.class);
        descriptor.add(outputStreamDescription().name("out").build());

        ComponentConfiguration configuration = new ComponentConfiguration(randomAlphabetic(6), descriptor);
        return configuration;
    }

    private ComponentConfiguration createComponentConfiguration() {
        ComponentDescription descriptor = new ComponentDescription(TestComponent.class);
        return new ComponentConfiguration(randomAlphabetic(6), descriptor);
    }

    public static class TestComponent implements Component {
        @Override
        public void init() {

        }
    }

    public static class TestComponentWithInputStreams implements Component {

        @InputStream(name = "in1")
        public Stream in1;

        @InputStream(name = "in2")
        public Stream in2;

        @Override
        public void init() {

        }
    }

    public static class TestComponentWithOuputStream implements Component {

        @OutputStream(from = "", name = "out")
        public Stream out;

        @Override
        public void init() {

        }
    }

}
