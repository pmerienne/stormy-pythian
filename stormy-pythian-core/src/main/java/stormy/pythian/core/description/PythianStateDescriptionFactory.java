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

import static com.google.common.base.Preconditions.checkArgument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.component.PythianState;

@Component
public class PythianStateDescriptionFactory {

	@Autowired
	private PropertyDescriptionFactory propertyDeclarationFactory;

	public PythianStateDescription createDescription(Class<? extends PythianState> clazz) {
		checkArgument(clazz != null, "PythianState class is mandatory");

		Documentation documentation = clazz.getAnnotation(Documentation.class);
		checkArgument(documentation != null, "No documentation found for " + clazz + " but documentation is mandatory!");

		PythianStateDescription description = new PythianStateDescription(clazz, documentation.name(), documentation.description());
		description.addProperties(propertyDeclarationFactory.createPropertyDeclarations(clazz));

		return description;
	}
}
