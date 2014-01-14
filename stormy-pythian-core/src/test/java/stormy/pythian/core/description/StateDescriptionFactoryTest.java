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
package stormy.pythian.core.description;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import storm.trident.state.StateFactory;
import stormy.pythian.model.annotation.State;
import stormy.pythian.model.component.Component;

@SuppressWarnings("serial")
@RunWith(MockitoJUnitRunner.class)
public class StateDescriptionFactoryTest {

	@InjectMocks
	private StateDescriptionFactory factory;

	@Test
	public void should_create_descriptions() {
		// Given
		class TestComponent implements Component {

			@State(name = "Word count", description = "State containing count by word")
			private StateFactory stateFactory;

			@Override
			public void init() {
			}
		}

		// When
		List<StateDescription> actualDescriptions = factory.createDescriptions(TestComponent.class);

		// Then
		assertThat(actualDescriptions).containsOnly(new StateDescription("Word count", "State containing count by word"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_fail_if_state_annotation_is_not_on_a_state_factory_field() {
		// Given
		class TestComponent implements Component {

			@State(name = "Word count")
			private Object stateFactory;

			@Override
			public void init() {
			}
		}

		// When
		factory.createDescriptions(TestComponent.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_fail_with_duplicated_state_names() {
		// Given
		class TestComponent implements Component {

			@State(name = "Word count")
			private StateFactory stateFactory1;

			@State(name = "Word count")
			private StateFactory stateFactory2;

			@Override
			public void init() {
			}
		}

		// When
		factory.createDescriptions(TestComponent.class);
	}
}
