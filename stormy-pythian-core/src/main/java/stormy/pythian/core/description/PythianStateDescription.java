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

import stormy.pythian.model.component.PythianState;

public class PythianStateDescription {

	private Class<? extends PythianState> clazz;

	private String name;
	private String description;

	private List<PropertyDescription> properties = new ArrayList<>();

	public PythianStateDescription() {
	}

	public PythianStateDescription(Class<? extends PythianState> clazz, String name, String description) {
		this.clazz = clazz;
		this.name = name;
		this.description = description;
	}

	public PythianStateDescription(Class<? extends PythianState> clazz, String name) {
		this.clazz = clazz;
		this.name = name;
		this.description = null;
	}

	public void addProperties(List<PropertyDescription> descriptions) {
		this.properties.addAll(descriptions);
	}

	public Class<? extends PythianState> getClazz() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<PropertyDescription> getProperties() {
		return properties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
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
		PythianStateDescription other = (PythianStateDescription) obj;
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PythianStateDescription [clazz=" + clazz + ", name=" + name + ", description=" + description + ", properties=" + properties + "]";
	}

}
