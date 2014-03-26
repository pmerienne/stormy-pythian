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
package stormy.pythian.core.topology;

import static stormy.pythian.core.utils.ReflectionHelper.setProperties;

import java.util.HashMap;
import java.util.Map;

import storm.trident.state.StateFactory;
import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.core.configuration.PythianStateConfiguration;
import stormy.pythian.model.component.PythianState;

public class PythianStateFactory {

	public Map<String, StateFactory> createStateFactories(ComponentConfiguration componentConfiguration) {
		Map<String, StateFactory> stateFactoriesByIds = new HashMap<>();

		for (PythianStateConfiguration stateConfiguration : componentConfiguration.getStates()) {
			PythianState pythianState = this.createStateFactory(stateConfiguration);
			StateFactory stateFactory = pythianState.createStateFactory();
			stateFactoriesByIds.put(stateConfiguration.getName(), stateFactory);
		}

		return stateFactoriesByIds;
	}

	private PythianState createStateFactory(PythianStateConfiguration configuration) {
		try {
			PythianState pythianState = configuration.retrieveImplementation().newInstance();
			setProperties(pythianState, configuration.getProperties());

			return pythianState;
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to create PythianState " + configuration.getDescription(), e);
		}
	}
}
