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
package stormy.pythian.service.description;

import static com.google.common.collect.Sets.newHashSet;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import storm.trident.state.StateFactory;
import stormy.pythian.core.description.ComponentDescription;
import stormy.pythian.core.description.ComponentDescriptionFactory;
import stormy.pythian.core.description.PythianStateDescription;
import stormy.pythian.core.description.PythianStateDescriptionFactory;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.component.PythianState;

@SuppressWarnings({ "serial", "unchecked" })
@RunWith(MockitoJUnitRunner.class)
public class DescriptionServiceTest {

	@InjectMocks
	private DescriptionService service;

	@Mock
	private ComponentDescriptionFactory componentDescriptionFactory;

	@Mock
	private PythianStateDescriptionFactory stateDescriptionFactory;

	@Mock
	private ClassRepository classRepository;

	@Test
	public void should_load_component_descriptions() {
		// Given
		when(classRepository.getComponentClasses()).thenReturn(newHashSet(TestAnalytics.class, TestLearner1.class, TestLearner2.class));

		ComponentDescription testAnalyticsDescription = mock(ComponentDescription.class);
		ComponentDescription testLearner1Description = mock(ComponentDescription.class);
		ComponentDescription testLearner2Description = mock(ComponentDescription.class);

		when(componentDescriptionFactory.createDeclaration(TestAnalytics.class)).thenReturn(testAnalyticsDescription);
		when(componentDescriptionFactory.createDeclaration(TestLearner1.class)).thenReturn(testLearner1Description);
		when(componentDescriptionFactory.createDeclaration(TestLearner2.class)).thenReturn(testLearner2Description);

		// When
		List<ComponentDescription> actualDescriptions = service.findAllComponentDescriptions();

		// Then
		assertThat(actualDescriptions).containsOnly(testAnalyticsDescription, testLearner1Description, testLearner2Description);
	}

	@Test
	public void should_load_state_descriptions() {
		// Given
		when(classRepository.getStateClasses()).thenReturn(newHashSet(State1.class, State2.class));

		PythianStateDescription state1Description = mock(PythianStateDescription.class);
		PythianStateDescription state2Description = mock(PythianStateDescription.class);

		when(stateDescriptionFactory.createDescription(State1.class)).thenReturn(state1Description);
		when(stateDescriptionFactory.createDescription(State2.class)).thenReturn(state2Description);

		// When
		List<PythianStateDescription> actualDescriptions = service.findAllStateDescriptions();

		// Then
		assertThat(actualDescriptions).containsOnly(state1Description, state2Description);
	}

	private static class TestAnalytics implements Component {
		@Override
		public void init() {
		}
	}

	private static class TestLearner1 implements Component {
		@Override
		public void init() {
		}
	}

	private static class TestLearner2 implements Component {
		@Override
		public void init() {
		}
	}

	private static class State1 implements PythianState {
		@Override
		public StateFactory createStateFactory() {
			return null;
		}
	}

	private static class State2 implements PythianState {
		@Override
		public StateFactory createStateFactory() {
			return null;
		}
	}
}
