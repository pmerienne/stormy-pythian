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
import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.core.topology.AvailableComponentPool;
import stormy.pythian.core.topology.ComponentFactory;
import stormy.pythian.core.topology.PythianTopology;
import stormy.pythian.model.component.Component;

@RunWith(MockitoJUnitRunner.class)
public class PythianTopologyTest {

	@InjectMocks
	PythianTopology launcher;

	@Mock
	ComponentFactory componentFactory;

	@Mock
	AvailableComponentPool componentPool;

	@Test
	public void should_create_components() {
		// Given
		PythianToplogyConfiguration topologyConfiguration = new PythianToplogyConfiguration();

		ComponentConfiguration componentConfiguration = new ComponentConfiguration();
		topologyConfiguration.getComponents().add(componentConfiguration);

		Component component = mock(Component.class);
		Map<String, Stream> inputStreams = new HashMap<>();

		when(componentPool.isEmpty()).thenReturn(false, true);
		when(componentPool.getAvailableComponent()).thenReturn(componentConfiguration);
		when(componentPool.getAvailableInputStreams(componentConfiguration)).thenReturn(inputStreams);
		when(componentFactory.createComponent(componentConfiguration, inputStreams)).thenReturn(component);

		// Then
		launcher.build(topologyConfiguration);

		// Then
		verify(componentPool).registerBuildedComponent(component, componentConfiguration);
	}

}
