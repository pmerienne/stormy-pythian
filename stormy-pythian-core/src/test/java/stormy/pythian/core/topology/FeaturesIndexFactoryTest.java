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
import static java.util.UUID.randomUUID;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.core.configuration.ConnectionConfiguration;
import stormy.pythian.core.configuration.InputStreamConfiguration;
import stormy.pythian.core.configuration.OutputStreamConfiguration;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.model.instance.FeaturesIndex;

import com.google.common.collect.Table;

@RunWith(MockitoJUnitRunner.class)
public class FeaturesIndexFactoryTest {

	@InjectMocks
	private FeaturesIndexFactory factory;

	@Mock
	private Table<String, String, FeaturesIndex> outputFeaturesIndexes;

	@Mock
	private Table<String, String, FeaturesIndex> inputFeaturesIndexes;

	@Mock
	private PythianToplogyConfiguration topologyConfiguration;

	@Test
	public void should_create_input_stream_index_from_output_index() {
		// Given
		String fromComponentId = randomUUID().toString();
		String fromStreamName = "out";
		FeaturesIndex outputIndex = new FeaturesIndex();

		String componentId = randomUUID().toString();
		String streamName = "in";

		InputStreamConfiguration inputStream = mock(InputStreamConfiguration.class);
		when(inputStream.retrieveStreamName()).thenReturn(streamName);

		ComponentConfiguration component = mock(ComponentConfiguration.class);
		when(component.getId()).thenReturn(componentId);
		when(component.getInputStreams()).thenReturn(asList(inputStream));

		when(topologyConfiguration.findConnectionTo(componentId, "in")).thenReturn(new ConnectionConfiguration(fromComponentId, fromStreamName, componentId, streamName));
		when(outputFeaturesIndexes.get(fromComponentId, fromStreamName)).thenReturn(outputIndex);

		// When
		Map<String, FeaturesIndex> actualIndexes = factory.createInputFeaturesIndexes(component);

		// Then
		assertThat(actualIndexes).includes(entry(streamName, outputIndex));
		verify(inputFeaturesIndexes).put(componentId, streamName, outputIndex);
	}

	@Test
	public void should_create_input_stream_index_without_connection() {
		// Given
		String componentId = randomUUID().toString();
		String streamName = "in";

		List<String> expectedFeatures = asList("age", "viewCount");

		InputStreamConfiguration inputStream = mock(InputStreamConfiguration.class);
		when(inputStream.retrieveStreamName()).thenReturn(streamName);
		when(inputStream.retrieveStreamFeatures()).thenReturn(expectedFeatures);

		when(topologyConfiguration.findConnectionTo(componentId, "in")).thenReturn(null);

		ComponentConfiguration component = mock(ComponentConfiguration.class);
		when(component.getId()).thenReturn(componentId);
		when(component.getInputStreams()).thenReturn(asList(inputStream));

		// When
		Map<String, FeaturesIndex> actualIndexes = factory.createInputFeaturesIndexes(component);

		// Then
		FeaturesIndex expectedIndex = new FeaturesIndex(expectedFeatures);
		assertThat(actualIndexes).includes(entry(streamName, expectedIndex));
		verify(inputFeaturesIndexes).put(componentId, streamName, expectedIndex);
	}

	@Test
	public void should_create_output_stream_index() {
		// Given
		String componentId = randomUUID().toString();
		String streamName = "out";

		Collection<String> newFeatures = asList("user age", "user name", "user id");

		OutputStreamConfiguration outputStream = mock(OutputStreamConfiguration.class);
		when(outputStream.retrieveStreamName()).thenReturn(streamName);
		when(outputStream.hasInputStreamSource()).thenReturn(false);
		when(outputStream.retrieveNewFeatures()).thenReturn(newFeatures);

		ComponentConfiguration component = mock(ComponentConfiguration.class);
		when(component.getId()).thenReturn(componentId);
		when(component.getOutputStreams()).thenReturn(asList(outputStream));

		// When
		Map<String, FeaturesIndex> actualIndexes = factory.createOutputFeaturesIndexes(component);

		// Then
		FeaturesIndex expectedIndex = new FeaturesIndex(newFeatures);
		assertThat(actualIndexes).includes(entry(streamName, expectedIndex));
		verify(outputFeaturesIndexes).put(componentId, streamName, expectedIndex);
	}

	@Test
	public void should_create_output_stream_index_from_input_stream() {
		// Given
		String componentId = randomUUID().toString();
		String streamName = "out";

		String inputStreamName = "in";
		List<String> inputFeatures = asList("user id", "rating");
		FeaturesIndex inputIndex = new FeaturesIndex(inputFeatures);

		Collection<String> newFeatures = asList("mean rating");

		OutputStreamConfiguration outputStream = mock(OutputStreamConfiguration.class);
		when(outputStream.retrieveStreamName()).thenReturn(streamName);
		when(outputStream.hasInputStreamSource()).thenReturn(true);
		when(outputStream.retrieveInputStreamSource()).thenReturn(inputStreamName);
		when(outputStream.retrieveNewFeatures()).thenReturn(newFeatures);

		when(inputFeaturesIndexes.get(componentId, inputStreamName)).thenReturn(inputIndex);

		ComponentConfiguration component = mock(ComponentConfiguration.class);
		when(component.getId()).thenReturn(componentId);
		when(component.getOutputStreams()).thenReturn(asList(outputStream));

		// When
		Map<String, FeaturesIndex> actualIndexes = factory.createOutputFeaturesIndexes(component);

		// Then
		FeaturesIndex expectedIndex = new FeaturesIndex(asList("user id", "rating", "mean rating"));
		assertThat(actualIndexes).includes(entry(streamName, expectedIndex));
		verify(outputFeaturesIndexes).put(componentId, streamName, expectedIndex);
	}

}
