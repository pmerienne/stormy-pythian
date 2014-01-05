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
import static stormy.pythian.model.annotation.ComponentType.STREAM_SOURCE;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import stormy.pythian.model.annotation.ComponentType;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.component.Component;

@SuppressWarnings("serial")
@RunWith(MockitoJUnitRunner.class)
public class ComponentDescriptionFactoryTest {

	@InjectMocks
	private ComponentDescriptionFactory factory;

	@Mock
	private PropertyDescriptionFactory propertyDeclarationFactory;

	@Mock
	private InputStreamDescriptionFactory inputStreamDeclarationFactory;

	@Mock
	private OutputStreamDescriptionFactory outputStreamDeclarationFactory;
	
	@Test(expected = IllegalArgumentException.class)
	public void should_throw_illegal_argument_exception_when_no_component_class() {
		// When
		factory.createDeclaration(null);
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
		factory.createDeclaration(TestComponent.class);
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
		ComponentDescription actualDeclaration = factory.createDeclaration(TestComponent.class);

		// Then
		assertThat(actualDeclaration.name).isEqualTo("Test component");
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
		ComponentDescription actualDeclaration = factory.createDeclaration(TestComponent.class);

		// Then
		assertThat(actualDeclaration.description).isEqualTo("Only for test purpose");
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
		ComponentDescription actualDeclaration = factory.createDeclaration(TestComponent.class);

		// Then
		assertThat(actualDeclaration.description).isEmpty();
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
		ComponentDescription actualDeclaration = factory.createDeclaration(TestComponent.class);

		// Then
		assertThat(actualDeclaration.getType()).isEqualTo(ComponentType.STREAM_SOURCE);
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
		ComponentDescription actualDeclaration = factory.createDeclaration(TestComponent.class);

		// Then
		assertThat(actualDeclaration.getType()).isEqualTo(ComponentType.NO_TYPE);
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
		ComponentDescription actualDeclaration = factory.createDeclaration(TestComponent.class);

		// Then
		assertThat(actualDeclaration.clazz).isEqualTo(TestComponent.class);
	}

}
