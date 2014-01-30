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
package stormy.pythian.core.configuration;

import java.util.List;

import stormy.pythian.core.description.PythianStateDescription;
import stormy.pythian.model.component.PythianState;

public class PythianStateConfiguration {

	private final String id;

	private final PythianStateDescription description;
	private final List<PropertyConfiguration> properties;

	public PythianStateConfiguration(String id, PythianStateDescription description, List<PropertyConfiguration> properties) {
		this.id = id;
		this.description = description;
		this.properties = properties;
	}

	public String getId() {
		return id;
	}

	public PythianStateDescription getDescription() {
		return description;
	}

	public List<PropertyConfiguration> getProperties() {
		return properties;
	}

	public Class<? extends PythianState> getImplementation() {
		return description.getClazz();
	}

}
