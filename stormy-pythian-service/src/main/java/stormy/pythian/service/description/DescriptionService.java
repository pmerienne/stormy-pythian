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
package stormy.pythian.service.description;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stormy.pythian.core.description.ComponentDescription;
import stormy.pythian.core.description.ComponentDescriptionFactory;
import stormy.pythian.model.annotation.ComponentType;
import stormy.pythian.model.component.Component;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

@Service
public class DescriptionService {

	@Autowired
	private ComponentDescriptionFactory descriptionFactory;

	@Autowired
	private ClassRepository classRepository;

	public Map<ComponentType, Collection<ComponentDescription>> findAllComponentDescriptions() {
		Set<Class<? extends Component>> componentClasses = classRepository.getComponentClasses();
		Multimap<ComponentType, ComponentDescription> componentDescriptions = HashMultimap.create();

		for (Class<? extends Component> componentClass : componentClasses) {
			ComponentDescription description = descriptionFactory.createDeclaration(componentClass);
			componentDescriptions.put(description.getType(), description);
		}

		return componentDescriptions.asMap();
	}
}
