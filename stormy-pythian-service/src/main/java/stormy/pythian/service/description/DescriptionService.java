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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stormy.pythian.core.description.ComponentDescription;
import stormy.pythian.core.description.ComponentDescriptionFactory;
import stormy.pythian.core.description.PythianStateDescription;
import stormy.pythian.core.description.PythianStateDescriptionFactory;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.component.PythianState;

@Service
public class DescriptionService {

	@Autowired
	private ComponentDescriptionFactory componentDescriptionFactory;

	@Autowired
	private PythianStateDescriptionFactory stateDescriptionFactory;

	@Autowired
	private ClassRepository classRepository;

	public List<ComponentDescription> findAllComponentDescriptions() {
		Set<Class<? extends Component>> classes = classRepository.getComponentClasses();
		List<ComponentDescription> descriptions = new ArrayList<>();

		for (Class<? extends Component> componentClass : classes) {
			try {
				ComponentDescription description = componentDescriptionFactory.createDeclaration(componentClass);
				descriptions.add(description);
			} catch (Exception ex) {
				// TODO log it, lazy bastard !!
				ex.printStackTrace();
			}
		}

		return descriptions;
	}

	public List<PythianStateDescription> findAllStateDescriptions() {
		Set<Class<? extends PythianState>> classes = classRepository.getStateClasses();
		List<PythianStateDescription> descriptions = new ArrayList<>();

		for (Class<? extends PythianState> clazz : classes) {
			try {
				PythianStateDescription description = stateDescriptionFactory.createDescription(clazz);
				descriptions.add(description);
			} catch (Exception ex) {
				// TODO log it, lazy bastard !!
				ex.printStackTrace();
			}
		}

		return descriptions;
	}
}
