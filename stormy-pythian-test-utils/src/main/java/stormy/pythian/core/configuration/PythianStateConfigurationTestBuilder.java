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

import java.util.ArrayList;
import java.util.List;

import stormy.pythian.core.description.PythianStateDescription;

public class PythianStateConfigurationTestBuilder {

	private String name;
	private PythianStateDescription description;
	private List<PropertyConfiguration> properties = new ArrayList<>();

	public static PythianStateConfigurationTestBuilder stateConfiguration(PythianStateDescription description) {
		PythianStateConfigurationTestBuilder builder = new PythianStateConfigurationTestBuilder();
		builder.description = description;
		return builder;
	}
	
	public PythianStateConfigurationTestBuilder name(String name) {
		this.name = name;
		return this;
	}

	public PythianStateConfigurationTestBuilder with(String name, Object value) {
		this.properties.add(new PropertyConfiguration(name, value));
		return this;
	}

	public PythianStateConfiguration build() {
		PythianStateConfiguration configuration = new PythianStateConfiguration();
		configuration.setName(name);
		configuration.setDescription(description);
		configuration.setProperties(properties);
		return configuration;
	}
}
