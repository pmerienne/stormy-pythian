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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import storm.trident.Stream;
import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.core.configuration.ConnectionConfiguration;
import stormy.pythian.core.description.ComponentDescription;
import stormy.pythian.core.topology.AvailableComponentPool;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.component.Component;


@SuppressWarnings("serial")
public class AvailableComponentPoolTest {

	@Test
	public void should_retrieve_available_component() {
		// Given
		ComponentDescription descriptor = new ComponentDescription(TestComponent.class);
		ComponentConfiguration configuration = new ComponentConfiguration();
		configuration.descriptor = descriptor;

		List<ComponentConfiguration> configurations = Arrays.asList(configuration);

		AvailableComponentPool pool = new AvailableComponentPool(configurations, new ArrayList<ConnectionConfiguration>());

		// When
		ComponentConfiguration actualComponent = pool.getAvailableComponent();

		// Then
		assertThat(actualComponent).isEqualTo(configuration);
	}

	@Test
	public void should_know_when_empty() {
		// Given
		ComponentDescription descriptor = new ComponentDescription(TestComponent.class);
		ComponentConfiguration configuration = new ComponentConfiguration();
		configuration.descriptor = descriptor;

		List<ComponentConfiguration> configurations = Arrays.asList(configuration);

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
		ComponentDescription descriptor = new ComponentDescription(TestComponentWithInputStreams.class);
		ComponentConfiguration configuration = new ComponentConfiguration();
		configuration.descriptor = descriptor;

		List<ComponentConfiguration> configurations = Arrays.asList(configuration);

		AvailableComponentPool pool = new AvailableComponentPool(configurations, new ArrayList<ConnectionConfiguration>());

		// When
		ComponentConfiguration actualComponent = pool.getAvailableComponent();

		// Then
		assertThat(actualComponent).isNull();
	}

	@Test
	public void should_register_builded_component() {
		// Given
		ComponentConfiguration componentWithOutputStream = createConfiguration(TestComponentWithOuputStream.class);
		ComponentConfiguration componentWithInputStreams = createConfiguration(TestComponentWithInputStreams.class);

		List<ComponentConfiguration> configurations = Arrays.asList(componentWithInputStreams, componentWithOutputStream);

		List<ConnectionConfiguration> connections = new ArrayList<ConnectionConfiguration>();
		connections.add(new ConnectionConfiguration(componentWithOutputStream.id, "out", componentWithInputStreams.id, "in1"));
		connections.add(new ConnectionConfiguration(componentWithOutputStream.id, "out", componentWithInputStreams.id, "in2"));

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
		ComponentConfiguration streamSourceConfiguration = createConfiguration(TestComponentWithOnlyOutput.class);
		TestComponentWithOnlyOutput streamSource = new TestComponentWithOnlyOutput();
		streamSource.out = mock(Stream.class);

		ComponentConfiguration componentConfiguration = createConfiguration(TestComponentWithInputStreams.class);
		List<ComponentConfiguration> configurations = asList(componentConfiguration);

		List<ConnectionConfiguration> connections = new ArrayList<ConnectionConfiguration>();
		connections.add(new ConnectionConfiguration(streamSourceConfiguration.id, "out", componentConfiguration.id, "in1"));
		connections.add(new ConnectionConfiguration(streamSourceConfiguration.id, "out", componentConfiguration.id, "in2"));

		AvailableComponentPool pool = new AvailableComponentPool(configurations, connections);
		pool.registerBuildedComponent(streamSource, streamSourceConfiguration);

		// When
		Map<String, Stream> actualInputStreams = pool.getAvailableInputStreams(componentConfiguration);

		// Then
		assertThat(actualInputStreams).includes(entry("in1", streamSource.out), entry("in2", streamSource.out));
	}

	private ComponentConfiguration createConfiguration(Class<? extends Component> clazz) {
		ComponentDescription descriptor = new ComponentDescription(clazz);
		ComponentConfiguration component = new ComponentConfiguration();
		component.id = randomAlphabetic(6);
		component.descriptor = descriptor;

		return component;
	}

	public static class TestComponent implements Component {
		@Override
		public void init() {

		}
	}

	public static class TestComponentWithInputStreams implements Component {

		@InputStream(expectedFeatures = {}, name = "in1")
		public Stream in1;

		@InputStream(expectedFeatures = {}, name = "in2")
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

	public static class TestComponentWithOnlyOutput implements Component {

		@OutputStream(from = "", name = "out")
		public Stream out;

		@Override
		public void init() {

		}
	}
}
