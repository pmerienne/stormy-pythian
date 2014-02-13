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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.annotation.PropertyType;

import com.google.common.collect.Lists;

@Component
public class PropertyDescriptionFactory {

	@SuppressWarnings("unchecked")
	public List<PropertyDescription> createPropertyDeclarations(Class<?> componentClass) {
		List<PropertyDescription> propertyDeclarations = new ArrayList<>();

		Set<Field> propertyFields = getAllFields(componentClass, withAnnotation(Property.class));
		for (Field propertyField : propertyFields) {
			Property property = propertyField.getAnnotation(Property.class);
			PropertyDescription propertyDeclaration = new PropertyDescription();
			propertyDeclaration.name = property.name();
			propertyDeclaration.description = property.description();
			propertyDeclaration.mandatory = property.mandatory();
			propertyDeclaration.type = PropertyType.fromType(propertyField.getType());

			propertyDeclarations.add(propertyDeclaration);
		}

		ensureNoDuplicatedProperty(propertyDeclarations);
		return propertyDeclarations;
	}

	private void ensureNoDuplicatedProperty(List<PropertyDescription> declarations) {
		Set<String> duplicatedNames = new HashSet<>();
		Set<String> uniqueNames = new HashSet<>();

		for (PropertyDescription declaration : declarations) {
			if (uniqueNames.contains(declaration.name)) {
				duplicatedNames.add(declaration.name);
			} else {
				uniqueNames.add(declaration.name);
			}
		}

		if (!duplicatedNames.isEmpty()) {
			throw new IllegalArgumentException("Property should have unique name. Found duplicates : " + Lists.newArrayList(duplicatedNames));
		}
	}
}
