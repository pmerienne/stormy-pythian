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
package stormy.pythian.model.annotation;

import static org.fest.assertions.Assertions.assertThat;
import static stormy.pythian.model.annotation.PropertyType.DECIMAL;
import static stormy.pythian.model.annotation.PropertyType.ENUM;
import static stormy.pythian.model.annotation.PropertyType.INTEGER;
import static stormy.pythian.model.annotation.PropertyType.NUMBER;
import static stormy.pythian.model.annotation.PropertyType.STRING;
import org.junit.Test;

public class PropertyTypeTest {

    @Test
    public void enum_should_support_any_enum() {
        assertThat(ENUM.support(TestEnum.class)).isTrue();
    }

    @Test
    public void number_should_support_any_numbers() {
        assertThat(NUMBER.support(Double.class)).isTrue();
        assertThat(NUMBER.support(Float.class)).isTrue();
        assertThat(NUMBER.support(Long.class)).isTrue();
        assertThat(NUMBER.support(Integer.class)).isTrue();
        assertThat(NUMBER.support(Number.class)).isTrue();

        assertThat(NUMBER.support(String.class)).isFalse();
    }

    @Test
    public void decimal_should_not_support_any_numbers() {
        assertThat(DECIMAL.support(Double.class)).isTrue();
        assertThat(DECIMAL.support(Float.class)).isTrue();
        assertThat(DECIMAL.support(Long.class)).isFalse();
        assertThat(DECIMAL.support(Integer.class)).isFalse();
        assertThat(DECIMAL.support(Number.class)).isFalse();
    }

    @Test
    public void number_should_not_support_primitive() {
        assertThat(NUMBER.support(double.class)).isFalse();
        assertThat(NUMBER.support(float.class)).isFalse();
        assertThat(NUMBER.support(long.class)).isFalse();
        assertThat(NUMBER.support(int.class)).isFalse();
    }

    @Test
    public void should_retrieve_expected_types() {
        assertThat(PropertyType.fromType(Double.class)).isEqualTo(DECIMAL);
        assertThat(PropertyType.fromType(Float.class)).isEqualTo(DECIMAL);
        assertThat(PropertyType.fromType(Integer.class)).isEqualTo(INTEGER);
        assertThat(PropertyType.fromType(Long.class)).isEqualTo(INTEGER);
        assertThat(PropertyType.fromType(Number.class)).isEqualTo(NUMBER);
        
        assertThat(PropertyType.fromType(String.class)).isEqualTo(STRING);
    }
    
    private static enum TestEnum {

    }
}
