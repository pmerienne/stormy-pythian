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
import static stormy.pythian.model.annotation.PropertyType.STRING;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
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
    public void should_retrieve_single_property_description() {
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
        List<PropertyDescription> actualDescriptions = factory.createPropertyDescriptions(TestComponent.class);

        // Then
        assertThat(actualDescriptions).hasSize(1);

        PropertyDescription actualPropertyDescription = actualDescriptions.get(0);
        assertThat(actualPropertyDescription).isEqualTo(
                new PropertyDescription("expected property", "tested property", false, STRING));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_illegal_argument_exception_with_unsupported_property_description_type() {
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
        factory.createPropertyDescriptions(TestComponent.class);
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
        factory.createPropertyDescriptions(TestComponent.class);
    }

}
