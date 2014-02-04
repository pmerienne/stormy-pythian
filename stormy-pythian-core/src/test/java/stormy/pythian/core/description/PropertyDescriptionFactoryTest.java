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

import org.junit.Before;
import org.junit.Test;


import stormy.pythian.core.description.PropertyDescription;
import stormy.pythian.core.description.PropertyDescriptionFactory;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.component.Component;

@SuppressWarnings("serial")
public class PropertyDescriptionFactoryTest {

	private PropertyDescriptionFactory factory;

	@Before
	public void init() {
		factory = new PropertyDescriptionFactory();
	}

	@Test
	public void should_retrieve_single_property_declaration() {
		// Given
		@Documentation(name = "Test component")
		class TestComponent implements Component {

			@Property(name = "expected property", description = "tested property", mandatory = false)
			public String expectedProperty;

			@Override
			public void init() {

			}
		}

		// When
		List<PropertyDescription> actualDeclarations = factory.createPropertyDeclarations(TestComponent.class);

		// Then
		assertThat(actualDeclarations).hasSize(1);

		PropertyDescription actualPropertyDeclaration = actualDeclarations.get(0);
		assertThat(actualPropertyDeclaration).isEqualTo(new PropertyDescription("expected property", "tested property", false, String.class));
	}

	@Test
	public void should_retrieve_primitives_property_declaration() {
		// Given
		@Documentation(name = "Test component")
		class TestComponent implements Component {

			@Property(name = "expected int")
			public int expectedInt;

			@Property(name = "expected double")
			public double expectedDouble;

			@Property(name = "expected float")
			public float expectedFloat;

			@Property(name = "expected long")
			public long expectedLong;

			@Property(name = "expected short")
			public short expectedShort;

			@Property(name = "expected byte")
			public byte expectedByte;

			@Property(name = "expected char")
			public char expectedChar;

			@Override
			public void init() {
			}
		}

		// When
		List<PropertyDescription> actualDeclarations = factory.createPropertyDeclarations(TestComponent.class);

		// Then
		assertThat(actualDeclarations).hasSize(7);

		assertThat(actualDeclarations).contains(new PropertyDescription("expected int", "", true, int.class));
		assertThat(actualDeclarations).contains(new PropertyDescription("expected double", "", true, double.class));
		assertThat(actualDeclarations).contains(new PropertyDescription("expected float", "", true, float.class));
		assertThat(actualDeclarations).contains(new PropertyDescription("expected long", "", true, long.class));
		assertThat(actualDeclarations).contains(new PropertyDescription("expected short", "", true, short.class));
		assertThat(actualDeclarations).contains(new PropertyDescription("expected byte", "", true, byte.class));
		assertThat(actualDeclarations).contains(new PropertyDescription("expected char", "", true, char.class));
	}

	@Test
	public void should_retrieve_enum_property_declaration() {
		// Given
		@Documentation(name = "Test component")
		class TestComponent implements Component {

			@Property(name = "expected enum")
			public TestEnum expectedEnum;

			@Override
			public void init() {
			}
		}

		// When
		List<PropertyDescription> actualDeclarations = factory.createPropertyDeclarations(TestComponent.class);

		// Then
		assertThat(actualDeclarations).containsOnly(new PropertyDescription("expected enum", "", true, TestEnum.class));
	}

	@Test
	public void should_retrieve_array_property_declaration() {
		// Given
		@Documentation(name = "Test component")
		class TestComponent implements Component {

			@Property(name = "expected ints")
			public int[] expectedInts;

			@Property(name = "expected strings")
			public String[] expectedStrings;

			@Override
			public void init() {
			}
		}

		// When
		List<PropertyDescription> actualDeclarations = factory.createPropertyDeclarations(TestComponent.class);

		// Then
		assertThat(actualDeclarations).hasSize(2);

		assertThat(actualDeclarations).contains(new PropertyDescription("expected ints", "", true, int[].class));
		assertThat(actualDeclarations).contains(new PropertyDescription("expected strings", "", true, String[].class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_illegal_argument_exception_with_unsupported_property_declaration_type() {
		// Given
		@Documentation(name = "Test component")
		class TestComponent implements Component {

			@Property(name = "object")
			public Object object;

			@Override
			public void init() {
			}
		}

		// When
		factory.createPropertyDeclarations(TestComponent.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_illegal_argument_exception_when_properties_have_same_names() {
		// Given
		@Documentation(name = "Test component")
		class TestComponent implements Component {

			@Property(name = "same name")
			public String stringProperty;

			@Property(name = "same name")
			public int intProperty;

			@Override
			public void init() {
			}
		}

		// When
		factory.createPropertyDeclarations(TestComponent.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_illegal_argument_exception_with_unsupported_property_declaration_array_type() {
		// Given
		@Documentation(name = "Test component")
		class TestComponent implements Component {

			@Property(name = "objects")
			public Object[] objects;

			@Override
			public void init() {
			}
		}

		// When
		factory.createPropertyDeclarations(TestComponent.class);
	}

	private static enum TestEnum {
		YES, NO;
	}
}
