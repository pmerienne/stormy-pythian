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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import stormy.pythian.model.annotation.PropertyType;

public class PropertyDescriptionTestBuilder {

	private String name;
	private String description = "";

	private boolean mandatory = true;
	private PropertyType type;

	private List<String> acceptedValues = new ArrayList<>();

	public PropertyDescriptionTestBuilder(String name, PropertyType type) {
		this.name = name;
		this.type = type;
	}

	public static PropertyDescriptionTestBuilder propertyDescription(String name, PropertyType type) {
		return new PropertyDescriptionTestBuilder(name, type);
	}

	public PropertyDescriptionTestBuilder description(String description) {
		this.description = description;
		return this;
	}

	public PropertyDescriptionTestBuilder mandatory(boolean mandatory) {
		this.mandatory = mandatory;
		return this;
	}
	
	public PropertyDescriptionTestBuilder accepted(String... acceptedValues) {
		this.acceptedValues.addAll(asList(acceptedValues));
		return this;
	}

	public PropertyDescription build() {
		PropertyDescription propertyDescription = new PropertyDescription();
		propertyDescription.setName(name);
		propertyDescription.setDescription(description);
		propertyDescription.setMandatory(mandatory);
		propertyDescription.setType(type);
		propertyDescription.setAcceptedValues(acceptedValues);

		return propertyDescription;
	}
}
