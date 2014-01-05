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
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.component.Component;

public class ComponentDescriptionFactory {

	protected PropertyDescriptionFactory propertyDeclarationFactory = new PropertyDescriptionFactory();
	protected InputStreamDescriptionFactory inputStreamDeclarationFactory = new InputStreamDescriptionFactory();
	protected OutputStreamDescriptionFactory outputStreamDeclarationFactory = new OutputStreamDescriptionFactory();

	public ComponentDescription createDeclaration(Class<? extends Component> componentClass) {
		checkArgument(componentClass != null, "Component class is mandatory");

		Documentation documentation = componentClass.getAnnotation(Documentation.class);
		checkArgument(documentation != null, "No documentation found for " + componentClass + " but documentation is mandatory!");

		ComponentDescription description = new ComponentDescription(componentClass, documentation.name(), documentation.description(), documentation.type());
		description.propertyDeclarations.addAll(propertyDeclarationFactory.createPropertyDeclarations(componentClass));
		description.inputStreamDescriptions.addAll(inputStreamDeclarationFactory.createInputStreamDeclarations(componentClass));
		description.outputStreamDescriptions.addAll(outputStreamDeclarationFactory.createOutputStreamDeclarations(componentClass));

		outputStreamDeclarationFactory.ensureInputStreamReference(description.outputStreamDescriptions, description.inputStreamDescriptions);

		return description;
	}

}