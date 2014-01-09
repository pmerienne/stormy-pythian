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
package stormy.pythian.core.utils;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import stormy.pythian.core.configuration.PropertyConfiguration;
import stormy.pythian.model.annotation.Configuration;
import stormy.pythian.model.annotation.Mapper;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.annotation.Topology;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.InputFixedFeaturesMapper;
import backtype.storm.Config;

@SuppressWarnings("serial")
public class ReflectionHelperTest {

	@Test
	public void should_find_input_streams() {
		// Given
		class TestComponent implements Component {
			@InputStream(name = "in1", expectedFeatures = {})
			public Stream clock;

			@InputStream(name = "in2", expectedFeatures = {})
			public Stream words;

			@Override
			public void init() {
			}
		}

		// When
		List<String> actualInputStreams = ReflectionHelper.getInputStreamNames(TestComponent.class);

		// Then
		assertThat(actualInputStreams).containsOnly("in1", "in2");
	}

	@Test
	public void should_set_properties() {
		// Given
		class TestObject {
			@Property(name = "prop1")
			public String prop1;

			@Property(name = "prop2")
			public Integer prop2;

			@Property(name = "prop3")
			private Boolean prop3;
		}

		TestObject test = new TestObject();
		List<PropertyConfiguration> properties = Arrays.asList(new PropertyConfiguration("prop1", "hey"), new PropertyConfiguration("prop2", 3), new PropertyConfiguration("prop3", true));

		// When
		ReflectionHelper.setProperties(test, properties);

		// Then
		assertThat(test.prop1).isEqualTo("hey");
		assertThat(test.prop2).isEqualTo(3);
		assertThat(test.prop3).isEqualTo(true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_fail_when_setting_bad_properties() {
		// Given
		class TestObject {
			@Property(name = "prop1")
			public String prop1;
		}

		TestObject test = new TestObject();
		List<PropertyConfiguration> properties = Arrays.asList(new PropertyConfiguration("prop1", true));

		// When
		ReflectionHelper.setProperties(test, properties);
	}

	@Test
	public void should_set_input_streams() {
		// Given
		class TestComponent implements Component {

			@InputStream(name = "in1", expectedFeatures = {})
			public Stream test;

			@InputStream(name = "in2", expectedFeatures = {})
			public Stream words;

			@Override
			public void init() {
			}
		}

		TestComponent component = new TestComponent();
		Stream testStream = mock(Stream.class);
		Stream wordsStream = mock(Stream.class);
		Map<String, Stream> inputStreams = new HashMap<>();
		inputStreams.put("in1", testStream);
		inputStreams.put("in2", wordsStream);

		// When
		ReflectionHelper.setInputStreams(component, inputStreams);

		// Then
		assertThat(component.test).isSameAs(testStream);
		assertThat(component.words).isSameAs(wordsStream);
	}

	@Test
	public void should_find_output_stream() {
		// Given
		class TestComponent implements Component {

			@OutputStream(from = "", name = "words")
			public Stream out;

			@Override
			public void init() {
			}
		}
		TestComponent component = new TestComponent();
		component.out = mock(Stream.class);

		// When
		Stream actualStream = ReflectionHelper.getOutputStream(component, "words");

		// Then
		assertThat(actualStream).isEqualTo(component.out);
	}

	@Test
	public void should_set_topology() {
		// Given
		class TestComponent implements Component {

			@Topology
			private TridentTopology topology;

			@Override
			public void init() {
			}
		}

		TestComponent component = new TestComponent();
		TridentTopology topology = mock(TridentTopology.class);

		// When
		ReflectionHelper.setTopology(component, topology);

		// Then
		assertThat(component.topology).isEqualTo(topology);
	}

	@Test
	public void should_set_configuration() {
		// Given
		class TestComponent implements Component {

			@Configuration
			private Config config;

			@Override
			public void init() {
			}
		}

		TestComponent component = new TestComponent();
		Config config = mock(Config.class);

		// When
		ReflectionHelper.setConfiguration(component, config);

		// Then
		assertThat(component.config).isEqualTo(config);
	}

	@Test
	public void should_set_features_mapper() {
		// Given
		class TestComponent implements Component {

			@Mapper(stream = "in")
			public InputFixedFeaturesMapper mapper;

			@Override
			public void init() {
			}
		}

		TestComponent testComponent = new TestComponent();
		InputFixedFeaturesMapper mapper = mock(InputFixedFeaturesMapper.class);

		// When
		ReflectionHelper.setFeaturesMapper(testComponent, "in", mapper);

		// Then
		assertThat(testComponent.mapper).isEqualTo(mapper);

	}
}
