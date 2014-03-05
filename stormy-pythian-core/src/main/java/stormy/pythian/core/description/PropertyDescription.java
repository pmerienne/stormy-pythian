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

import java.util.ArrayList;
import java.util.List;

import stormy.pythian.model.annotation.PropertyType;

public class PropertyDescription {

	private String name;
	private String description;

	private boolean mandatory;
	private PropertyType type;

	private List<String> acceptedValues = new ArrayList<>();

	public PropertyDescription() {
	}

	public PropertyDescription(String name, String description, boolean mandatory, PropertyType type) {
		this.name = name;
		this.description = description;
		this.mandatory = mandatory;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public PropertyType getType() {
		return type;
	}

	public void setType(PropertyType type) {
		this.type = type;
	}

	public List<String> getAcceptedValues() {
		return acceptedValues;
	}

	public void setAcceptedValues(List<String> acceptedValues) {
		this.acceptedValues = acceptedValues;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acceptedValues == null) ? 0 : acceptedValues.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + (mandatory ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyDescription other = (PropertyDescription) obj;
		if (acceptedValues == null) {
			if (other.acceptedValues != null)
				return false;
		} else if (!acceptedValues.equals(other.acceptedValues))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (mandatory != other.mandatory)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PropertyDescription [name=" + name + ", description=" + description + ", mandatory=" + mandatory + ", type=" + type + ", acceptedValues=" + acceptedValues + "]";
	}

}
