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

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static stormy.pythian.core.configuration.StateFactoryConfiguration.TransactionType.NONE;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import storm.trident.state.StateFactory;
import stormy.pythian.core.configuration.InMemoryStateConfiguration;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.core.configuration.StateFactoryConfiguration;
import stormy.pythian.core.topology.PythianStateFactory.NoneTransactionalInMemoryStateFactory;

@RunWith(MockitoJUnitRunner.class)
public class PythianStateFactoryTest {

	@InjectMocks
	private PythianStateFactory factory;

	@Test
	public void should_create_state_factories() {
		// Given
		StateFactoryConfiguration expectedStateConfiguration = new InMemoryStateConfiguration(NONE);

		PythianToplogyConfiguration topologyConfiguration = mock(PythianToplogyConfiguration.class);
		when(topologyConfiguration.getStates()).thenReturn(Arrays.asList(expectedStateConfiguration));

		// When
		Map<String, StateFactory> actualStateFactories = factory.createStateFactories(topologyConfiguration);
		assertThat(actualStateFactories).includes(entry( //
				expectedStateConfiguration.getId(), //
				new NoneTransactionalInMemoryStateFactory(expectedStateConfiguration.getId()) //
				));
	}

	@Test
	public void should_create_in_memory_state_factory() {
		// Given
		InMemoryStateConfiguration configuration = new InMemoryStateConfiguration(NONE);

		// When
		StateFactory stateFactory = factory.createStateFactory(configuration);

		// Then
		assertThat(stateFactory).isEqualTo(new NoneTransactionalInMemoryStateFactory(configuration.getId()));
	}
}
