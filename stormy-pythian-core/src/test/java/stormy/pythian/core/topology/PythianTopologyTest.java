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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import storm.trident.Stream;
import storm.trident.state.StateFactory;
import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.FeaturesIndex;

@RunWith(MockitoJUnitRunner.class)
public class PythianTopologyTest {

	@InjectMocks
	PythianTopology topology;

	@Mock
	ComponentFactory componentFactory;

	@Mock
	AvailableComponentPool componentPool;

	@Mock
	private FeaturesIndexFactory featuresIndexFactory;

	@Mock
	private PythianStateFactory pythianStateFactory;

	@SuppressWarnings("unchecked")
	@Test
	public void should_create_components() {
		// Given
		String componentId = randomAlphabetic(6);

		PythianToplogyConfiguration topologyConfiguration = mock(PythianToplogyConfiguration.class);

		ComponentConfiguration componentConfiguration = mock(ComponentConfiguration.class);
		when(componentConfiguration.getId()).thenReturn(componentId);
		when(topologyConfiguration.getComponents()).thenReturn(asList(componentConfiguration));

		Map<String, StateFactory> expectedStateFactories = new HashMap<>();

		Component component = mock(Component.class);
		Map<String, Stream> inputStreams = new HashMap<>();

		Map<String, FeaturesIndex> inputFeaturesIndexes = mock(Map.class);
		Map<String, FeaturesIndex> outputFeaturesIndexes = mock(Map.class);

		when(componentPool.isEmpty()).thenReturn(false, true);
		when(componentPool.getAvailableComponent()).thenReturn(componentConfiguration);
		when(componentPool.getAvailableInputStreams(componentConfiguration)).thenReturn(inputStreams);
		when(featuresIndexFactory.createInputFeaturesIndexes(componentConfiguration)).thenReturn(inputFeaturesIndexes);
		when(featuresIndexFactory.createOutputFeaturesIndexes(componentConfiguration)).thenReturn(outputFeaturesIndexes);
		when(pythianStateFactory.createStateFactories(topologyConfiguration)).thenReturn(expectedStateFactories);
		when(componentFactory.createComponent(componentConfiguration, expectedStateFactories, inputStreams, inputFeaturesIndexes, outputFeaturesIndexes)).thenReturn(component);

		// Then
		topology.build(topologyConfiguration);

		// Then
		verify(componentPool).registerBuildedComponent(component, componentConfiguration);
	}

}
