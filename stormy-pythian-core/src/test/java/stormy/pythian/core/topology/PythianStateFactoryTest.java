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
import static java.util.Collections.EMPTY_LIST;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import storm.trident.state.StateFactory;
import stormy.pythian.core.configuration.PythianStateConfiguration;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.core.description.PythianStateDescription;
import stormy.pythian.model.component.PythianState;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class PythianStateFactoryTest {

	public static final StateFactory EXPECTED_STATE_FACTORY = mock(StateFactory.class);

	@InjectMocks
	private PythianStateFactory factory;

	@Test
	public void should_create_state_factory() {
		// Given
		final PythianStateDescription description = new PythianStateDescription(TestPythianState.class, "test");
		final PythianStateConfiguration expectedStateConfiguration = new PythianStateConfiguration("uuid", description, EMPTY_LIST);

		final PythianToplogyConfiguration topologyConfiguration = mock(PythianToplogyConfiguration.class);
		given(topologyConfiguration.getStates()).willReturn(asList(expectedStateConfiguration));

		// When
		Map<String, StateFactory> stateFactories = factory.createStateFactories(topologyConfiguration);

		// Then
		StateFactory actualStateFactory = stateFactories.get("uuid");
		assertThat(actualStateFactory).isSameAs(EXPECTED_STATE_FACTORY);
	}

	@SuppressWarnings("serial")
	public static class TestPythianState implements PythianState {

		@Override
		public StateFactory createStateFactory() {
			return EXPECTED_STATE_FACTORY;
		}

	}
}
