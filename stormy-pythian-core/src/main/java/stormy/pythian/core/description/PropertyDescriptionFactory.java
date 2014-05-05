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

import static org.reflections.ReflectionUtils.getAllFields;
import static org.reflections.ReflectionUtils.withAnnotation;
import static stormy.pythian.model.annotation.PropertyType.fromType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.annotation.PropertyType;

@Component
public class PropertyDescriptionFactory {

    @SuppressWarnings("unchecked")
    public List<PropertyDescription> createPropertyDescriptions(Class<?> componentClass) {
        List<PropertyDescription> propertyDescriptions = new ArrayList<>();

        Set<Field> propertyFields = getAllFields(componentClass, withAnnotation(Property.class));
        for (Field propertyField : propertyFields) {
            Property property = propertyField.getAnnotation(Property.class);
            PropertyDescription propertyDescription = new PropertyDescription(property.name(), property.description(), property.mandatory(), fromType(propertyField.getType()));
            if (PropertyType.ENUM.equals(propertyDescription.getType())) {
                List<String> enumValues = retrieveEnumValues(propertyField);
                propertyDescription.setAcceptedValues(enumValues);
            }

            propertyDescriptions.add(propertyDescription);
        }

        return propertyDescriptions;
    }

    private List<String> retrieveEnumValues(Field field) {
        Object[] enumConstants = field.getType().getEnumConstants();

        List<String> enumValues = new ArrayList<>(enumConstants.length);
        for (Object enumConstant : enumConstants) {
            enumValues.add(enumConstant.toString());
        }

        return enumValues;
    }
}
