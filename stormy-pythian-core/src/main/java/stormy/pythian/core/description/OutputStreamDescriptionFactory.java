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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import storm.trident.Stream;
import stormy.pythian.model.annotation.OutputStream;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Component
public class OutputStreamDescriptionFactory {

	@Autowired
	private FeatureDescriptionFactory featureDescriptorFactory;

	@SuppressWarnings("unchecked")
	public List<OutputStreamDescription> createOutputStreamDeclarations(Class<?> componentClass) {
		List<OutputStreamDescription> declarations = new ArrayList<>();

		Set<Field> fields = getFields(componentClass, withAnnotation(OutputStream.class));
		for (Field field : fields) {
			if (field.getType() != Stream.class) {
				throw new IllegalArgumentException(OutputStream.class + " can only be applied to " + Stream.class);
			}

			OutputStream annotation = field.getAnnotation(OutputStream.class);
			List<FeatureDescription> newFeatures = featureDescriptorFactory.createDescriptions(annotation);
			OutputStreamDescription declaration = new OutputStreamDescription(annotation.name(), annotation.from(), newFeatures);
			declarations.add(declaration);
		}

		ensureNoDuplicatedOutputStream(declarations);
		return declarations;
	}

	private void ensureNoDuplicatedOutputStream(List<OutputStreamDescription> declarations) {
		Set<String> duplicatedNames = new HashSet<>();
		Set<String> uniqueNames = new HashSet<>();

		for (OutputStreamDescription declaration : declarations) {
			if (uniqueNames.contains(declaration.getName())) {
				duplicatedNames.add(declaration.getName());
			} else {
				uniqueNames.add(declaration.getName());
			}
		}

		if (!duplicatedNames.isEmpty()) {
			throw new IllegalArgumentException("OutputStream should have unique name. Found duplicates : " + Lists.newArrayList(duplicatedNames));
		}
	}

	public void ensureInputStreamReference(List<OutputStreamDescription> outputStreamDeclarations, List<InputStreamDescription> inputStreamDeclarations) {
		for (OutputStreamDescription outputStreamDeclaration : outputStreamDeclarations) {
			if (!StringUtils.isEmpty(outputStreamDeclaration.getFrom())) {
				ensureInputStreamReference(outputStreamDeclaration.getFrom(), inputStreamDeclarations);
			}
		}
	}

	private void ensureInputStreamReference(String name, List<InputStreamDescription> declarations) {
		boolean inputStreamExists = Lists.transform(declarations, new Function<InputStreamDescription, String>() {
			public String apply(InputStreamDescription declaration) {
				return declaration.getName();
			}
		}).contains(name);

		if (!inputStreamExists) {
			throw new IllegalArgumentException("Output stream reference a not existing input stream : \"" + name + "\"");
		}
	}
}
