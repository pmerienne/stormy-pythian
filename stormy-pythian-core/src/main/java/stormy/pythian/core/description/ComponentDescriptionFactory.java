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

import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.component.Component;

@org.springframework.stereotype.Component
public class ComponentDescriptionFactory {

	@Autowired
	private PropertyDescriptionFactory propertyDeclarationFactory;

	@Autowired
	private InputStreamDescriptionFactory inputStreamDeclarationFactory;

	@Autowired
	private OutputStreamDescriptionFactory outputStreamDeclarationFactory;
	
	@Autowired
	private ReferencedStateDescriptionFactory stateDescriptionFactory;

	public ComponentDescription createDeclaration(Class<? extends Component> componentClass) {
		checkArgument(componentClass != null, "Component class is mandatory");

		Documentation documentation = componentClass.getAnnotation(Documentation.class);
		checkArgument(documentation != null, "No documentation found for " + componentClass + " but documentation is mandatory!");

		ComponentDescription description = new ComponentDescription(componentClass, documentation.name(), documentation.description(), documentation.type());
		description.addProperties(propertyDeclarationFactory.createPropertyDeclarations(componentClass));
		description.addInputStreams(inputStreamDeclarationFactory.createInputStreamDeclarations(componentClass));
		description.addOutputStreams(outputStreamDeclarationFactory.createOutputStreamDeclarations(componentClass));
		description.addStates(stateDescriptionFactory.createDescriptions(componentClass));
		
		description.ensureNoDuplicatedInputStreams();
		description.ensureOutputStreamReference();
		description.ensureNoDuplicatedOutputStreams();
		description.ensureNoDuplicatedProperties();
		description.ensureNoDuplicatedStateName();
		
		return description;
	}

}