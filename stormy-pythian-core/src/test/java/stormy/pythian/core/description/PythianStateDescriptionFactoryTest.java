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
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import storm.trident.state.StateFactory;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.component.PythianState;

@SuppressWarnings("serial")
@RunWith(MockitoJUnitRunner.class)
public class PythianStateDescriptionFactoryTest {

	@InjectMocks
	private PythianStateDescriptionFactory factory;

	@Mock
	private PropertyDescriptionFactory propertyDescriptionFactory;

	@Test
	public void should_create_description() {
		// Given
		@Documentation(name = "Name", description = "Description")
		class TestState implements PythianState {

			public StateFactory createStateFactory() {
				return null;
			}

		}

		List<PropertyDescription> expectedProperties = new ArrayList<>();
		given(propertyDescriptionFactory.createPropertyDeclarations(TestState.class)).willReturn(expectedProperties);

		// When
		PythianStateDescription description = factory.createDescription(TestState.class);

		// Then
		assertThat(description.getName()).isEqualTo("Name");
		assertThat(description.getDescription()).isEqualTo("Description");
		assertThat(description.getProperties()).isEqualTo(expectedProperties);
		assertThat(description.getClazz()).isEqualTo(TestState.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_fail_without_documentation() {
		// Given
		class TestState implements PythianState {

			public StateFactory createStateFactory() {
				return null;
			}

		}

		// When
		factory.createDescription(TestState.class);
	}
}
