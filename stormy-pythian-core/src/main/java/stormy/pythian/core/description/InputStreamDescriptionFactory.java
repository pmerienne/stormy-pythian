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

import static org.reflections.ReflectionUtils.getFields;
import static org.reflections.ReflectionUtils.withAnnotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import storm.trident.Stream;
import stormy.pythian.model.annotation.InputStream;

import com.google.common.collect.Lists;

@Component
public class InputStreamDescriptionFactory {

	@Autowired
	private FeatureDescriptionFactory featureDescriptorFactory;

	@SuppressWarnings("unchecked")
	public List<InputStreamDescription> createInputStreamDeclarations(Class<?> componentClass) {
		List<InputStreamDescription> inputStreamDeclarations = new ArrayList<>();

		Set<Field> inputStreamFields = getFields(componentClass, withAnnotation(InputStream.class));
		for (Field inputStreamField : inputStreamFields) {
			if (inputStreamField.getType() != Stream.class) {
				throw new IllegalArgumentException(InputStream.class + " can only be applied to " + Stream.class);
			}

			InputStreamDescription inputStreamDescription = createInputStreamDescription(inputStreamField);
			inputStreamDeclarations.add(inputStreamDescription);
		}

		ensureNoDuplicatedInputStream(inputStreamDeclarations);
		return inputStreamDeclarations;
	}

	private InputStreamDescription createInputStreamDescription(Field inputStreamField) {
		InputStream annotation = inputStreamField.getAnnotation(InputStream.class);

		InputStreamDescription inputStreamDescription = null;
		switch (annotation.type()) {
		case USER_SELECTION:
			inputStreamDescription = new InputStreamDescription(annotation.name(), annotation.type(), new ArrayList<FeatureDescription>());
			break;
		case FIXED_FEATURES:
			List<FeatureDescription> expectedFeatures = featureDescriptorFactory.createDescriptions(annotation);
			inputStreamDescription = new InputStreamDescription(annotation.name(), annotation.type(), expectedFeatures);
		}
		return inputStreamDescription;
	}

	private void ensureNoDuplicatedInputStream(List<InputStreamDescription> inputStreamDeclarations) {
		Set<String> duplicatedNames = new HashSet<>();
		Set<String> uniqueNames = new HashSet<>();

		for (InputStreamDescription declaration : inputStreamDeclarations) {
			if (uniqueNames.contains(declaration.getName())) {
				duplicatedNames.add(declaration.getName());
			} else {
				uniqueNames.add(declaration.getName());
			}
		}

		if (!duplicatedNames.isEmpty()) {
			throw new IllegalArgumentException("InputStream should have unique name. Found duplicates : " + Lists.newArrayList(duplicatedNames));
		}
	}

}
