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

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static stormy.pythian.model.annotation.ComponentType.STREAM_SOURCE;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import storm.trident.state.StateFactory;
import stormy.pythian.model.annotation.ComponentType;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.State;
import stormy.pythian.model.component.Component;

@SuppressWarnings("serial")
@RunWith(MockitoJUnitRunner.class)
public class ComponentDescriptionFactoryTest {

	@InjectMocks
	private ComponentDescriptionFactory factory;

	@Mock
	private PropertyDescriptionFactory propertyDescriptionFactory;

	@Mock
	private InputStreamDescriptionFactory inputStreamDescriptionFactory;

	@Mock
	private OutputStreamDescriptionFactory outputStreamDescriptionFactory;

	@Mock
	private ReferencedStateDescriptionFactory stateDescriptionFactory;
	
	@Test(expected = IllegalArgumentException.class)
	public void should_throw_illegal_argument_exception_when_no_component_class() {
		// When
		factory.createDescription(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_illegal_argument_exception_when_no_component_documentation() {
		// Given
		class TestComponent implements Component {
			@Override
			public void init() {

			}
		}

		// When
		factory.createDescription(TestComponent.class);
	}

	@Test
	public void should_retrieve_name() {
		// Given
		@Documentation(name = "Test component")
		class TestComponent implements Component {
			@Override
			public void init() {

			}
		}

		// When
		ComponentDescription actualDescription = factory.createDescription(TestComponent.class);

		// Then
		assertThat(actualDescription.getName()).isEqualTo("Test component");
	}

	@Test
	public void should_retrieve_description() {
		// Given
		@Documentation(name = "Test component", description = "Only for test purpose")
		class TestComponent implements Component {
			@Override
			public void init() {

			}
		}

		// When
		ComponentDescription actualDescription = factory.createDescription(TestComponent.class);

		// Then
		assertThat(actualDescription.getDescription()).isEqualTo("Only for test purpose");
	}

	@Test
	public void should_retrieve_empty_description_when_no_declared_description() {
		// Given
		@Documentation(name = "Test component")
		class TestComponent implements Component {
			@Override
			public void init() {

			}
		}

		// When
		ComponentDescription actualDescription = factory.createDescription(TestComponent.class);

		// Then
		assertThat(actualDescription.getDescription()).isEmpty();
	}

	@Test
	public void should_retrieve_component_type() {
		// Given
		@Documentation(name = "Test component", type = STREAM_SOURCE)
		class TestComponent implements Component {
			@Override
			public void init() {

			}
		}

		// When
		ComponentDescription actualDescription = factory.createDescription(TestComponent.class);

		// Then
		assertThat(actualDescription.getType()).isEqualTo(ComponentType.STREAM_SOURCE);
	}
	
	@Test
	public void should_retrieve_no_type() {
		// Given
		@Documentation(name = "Test component")
		class TestComponent implements Component {
			@Override
			public void init() {

			}
		}

		// When
		ComponentDescription actualDescription = factory.createDescription(TestComponent.class);

		// Then
		assertThat(actualDescription.getType()).isEqualTo(ComponentType.NO_TYPE);
	}

	@Test
	public void should_retrieve_class() {
		// Given
		@Documentation(name = "Test component")
		class TestComponent implements Component {
			@Override
			public void init() {

			}
		}

		// When
		ComponentDescription actualDescription = factory.createDescription(TestComponent.class);

		// Then
		assertThat(actualDescription.getClazz()).isEqualTo(TestComponent.class);
	}


	@Test(expected = IllegalArgumentException.class)
	public void should_fail_with_duplicated_state_names() {
		// Given
		@Documentation(name = "Test component")
		class TestComponent implements Component {

			@State(name = "word count")
			private StateFactory stateFactory1;

			@State(name = "word count")
			private StateFactory stateFactory2;

			@Override
			public void init() {
			}
		}
		
		List<ReferencedStateDescription> expectedStateDescriptions = asList(new ReferencedStateDescription("word count"), new ReferencedStateDescription("word count"));
		given(stateDescriptionFactory.createDescriptions(TestComponent.class)).willReturn(expectedStateDescriptions );

		// When
		factory.createDescription(TestComponent.class);
	}
}
