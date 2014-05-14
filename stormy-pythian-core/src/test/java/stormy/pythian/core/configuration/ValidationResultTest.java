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
import static stormy.pythian.core.configuration.ValidationResultTestBuilder.validationResult;
import org.junit.Test;

public class ValidationResultTest {

    @Test
    public void should_include_children_errors() {
        // Given
        ValidationResult propertyValidationResult1 = validationResult("Property 1").with("Missing value").build();
        ValidationResult propertyValidationResult2 = validationResult("Property 2").with("Missing value").build();
        ValidationResult propertyValidationResult3 = validationResult("Property 3").build();

        ValidationResult componentValidationResult = new ValidationResult("Component");

        // When
        componentValidationResult.include(propertyValidationResult1);
        componentValidationResult.include(propertyValidationResult2);
        componentValidationResult.include(propertyValidationResult3);

        // Then
        assertThat(componentValidationResult.getErrors()).containsOnly("Property 1 > Missing value", "Property 2 > Missing value");
    }
}
