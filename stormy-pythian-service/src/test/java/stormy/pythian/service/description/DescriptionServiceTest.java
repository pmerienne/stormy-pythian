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
import static stormy.pythian.model.annotation.ComponentType.ANALYTICS;
import static stormy.pythian.model.annotation.ComponentType.LEARNER;

import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import stormy.pythian.core.description.ComponentDescription;
import stormy.pythian.core.description.ComponentDescriptionFactory;
import stormy.pythian.model.annotation.ComponentType;
import stormy.pythian.model.component.Component;

@SuppressWarnings({ "serial", "unchecked" })
@RunWith(MockitoJUnitRunner.class)
public class DescriptionServiceTest {

	@InjectMocks
	private DescriptionService service;

	@Mock
	private ComponentDescriptionFactory descriptionFactory;

	@Mock
	private ClassRepository classRepository;

	@Test
	public void should_load_component_descriptions() {
		// Given
		when(classRepository.getComponentClasses()).thenReturn(newHashSet(TestAnalytics.class, TestLearner1.class, TestLearner2.class));

		ComponentDescription testAnalyticsDescription = mock(ComponentDescription.class);
		when(testAnalyticsDescription.getType()).thenReturn(ANALYTICS);
		ComponentDescription testLearner1Description = mock(ComponentDescription.class);
		when(testLearner1Description.getType()).thenReturn(LEARNER);
		ComponentDescription testLearner2Description = mock(ComponentDescription.class);
		when(testLearner2Description.getType()).thenReturn(LEARNER);

		when(descriptionFactory.createDeclaration(TestAnalytics.class)).thenReturn(testAnalyticsDescription);
		when(descriptionFactory.createDeclaration(TestLearner1.class)).thenReturn(testLearner1Description);
		when(descriptionFactory.createDeclaration(TestLearner2.class)).thenReturn(testLearner2Description);

		// When
		Map<ComponentType, Collection<ComponentDescription>> actualDescriptions = service.findAllComponentDescriptions();

		// Then
		assertThat(actualDescriptions.get(ANALYTICS)).contains(testAnalyticsDescription);
		assertThat(actualDescriptions.get(LEARNER)).contains(testLearner1Description, testLearner2Description);
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

}
