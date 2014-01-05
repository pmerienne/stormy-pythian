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

import stormy.pythian.model.annotation.ComponentType;
import stormy.pythian.model.component.Component;

public class ComponentDescription {

	public final Class<? extends Component> clazz;

	public final String name;
	public final String description;
	public final ComponentType type;

	public final List<PropertyDescription> propertyDeclarations = new ArrayList<>();

	public final List<OutputStreamDescription> outputStreamDescriptions = new ArrayList<>();
	public final List<InputStreamDescription> inputStreamDescriptions = new ArrayList<>();

	public ComponentDescription(Class<? extends Component> clazz) {
		this.name = "";
		this.description = "";
		this.clazz = clazz;
		this.type = ComponentType.NO_TYPE;
	}

	public ComponentDescription(Class<? extends Component> clazz, String name, String description, ComponentType type) {
		this.clazz = clazz;
		this.name = name;
		this.description = description;
		this.type = type;
	}

	public Class<? extends Component> getClazz() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public ComponentType getType() {
		return type;
	}

	public List<PropertyDescription> getPropertyDeclarations() {
		return propertyDeclarations;
	}

	public List<OutputStreamDescription> getOutputStreamDescriptions() {
		return outputStreamDescriptions;
	}

	public List<InputStreamDescription> getInputStreamDescriptions() {
		return inputStreamDescriptions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((inputStreamDescriptions == null) ? 0 : inputStreamDescriptions.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((outputStreamDescriptions == null) ? 0 : outputStreamDescriptions.hashCode());
		result = prime * result + ((propertyDeclarations == null) ? 0 : propertyDeclarations.hashCode());
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
		ComponentDescription other = (ComponentDescription) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (inputStreamDescriptions == null) {
			if (other.inputStreamDescriptions != null)
				return false;
		} else if (!inputStreamDescriptions.equals(other.inputStreamDescriptions))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (outputStreamDescriptions == null) {
			if (other.outputStreamDescriptions != null)
				return false;
		} else if (!outputStreamDescriptions.equals(other.outputStreamDescriptions))
			return false;
		if (propertyDeclarations == null) {
			if (other.propertyDeclarations != null)
				return false;
		} else if (!propertyDeclarations.equals(other.propertyDeclarations))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ComponentDescription [clazz=" + clazz + ", name=" + name + ", description=" + description + ", type=" + type + ", propertyDeclarations=" + propertyDeclarations
				+ ", outputStreamDescriptions=" + outputStreamDescriptions + ", inputStreamDescriptions=" + inputStreamDescriptions + "]";
	}

}
