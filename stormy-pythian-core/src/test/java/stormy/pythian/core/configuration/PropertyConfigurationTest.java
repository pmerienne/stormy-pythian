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
package stormy.pythian.core.configuration;

import static org.fest.assertions.Assertions.assertThat;
import static stormy.pythian.model.annotation.PropertyType.STRING;
import org.junit.Test;
import stormy.pythian.core.description.PropertyDescription;

public class PropertyConfigurationTest {

    @Test
    public void should_report_mandatory_with_null_value() {
        // Given
        PropertyConfiguration configuration = new PropertyConfiguration("maxBatchSize", null);
        configuration.setDescription(new PropertyDescription("Max batch size", "", true, STRING));

        // When
        ValidationResult actualResult = configuration.validate();

        // Then
        assertThat(actualResult.getErrors()).containsOnly("Property is mandatory");
    }

    @Test
    public void should_not_report_not_mandatory_with_null_value() {
        // Given
        PropertyConfiguration configuration = new PropertyConfiguration("maxBatchSize", null);
        configuration.setDescription(new PropertyDescription("Max batch size", "", false, STRING));

        // When
        ValidationResult actualResult = configuration.validate();

        // Then
        assertThat(actualResult.getErrors()).isEmpty();
    }
}
