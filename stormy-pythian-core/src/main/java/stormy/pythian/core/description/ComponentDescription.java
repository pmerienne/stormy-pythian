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

import static com.google.common.collect.Lists.transform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import stormy.pythian.model.annotation.ComponentType;
import stormy.pythian.model.component.Component;

public class ComponentDescription {

	private Class<? extends Component> clazz;

	private String name;
	private String description;
	private ComponentType type;

	private List<PropertyDescription> properties = new ArrayList<>();

	private List<OutputStreamDescription> outputStreams = new ArrayList<>();
	private List<InputStreamDescription> inputStreams = new ArrayList<>();

	private List<ReferencedStateDescription> states = new ArrayList<>();

	public ComponentDescription() {
	}

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

	public void addProperties(List<PropertyDescription> properties) {
		this.properties.addAll(properties);
	}

	public void addInputStreams(List<InputStreamDescription> inputStreams) {
		this.inputStreams.addAll(inputStreams);
	}

	public void addOutputStreams(List<OutputStreamDescription> outputStreams) {
		this.outputStreams.addAll(outputStreams);
	}

	public void addStates(List<ReferencedStateDescription> states) {
		this.states.addAll(states);
	}

	public void ensureOutputStreamReference() {
		for (OutputStreamDescription outputStream : outputStreams) {
			if (!StringUtils.isEmpty(outputStream.getFrom())) {
				ensureInputStreamReference(outputStream.getFrom());
			}
		}
	}

	public void ensureNoDuplicatedOutputStreams() {
		Set<String> duplicatedNames = new HashSet<>();
		Set<String> uniqueNames = new HashSet<>();

		for (OutputStreamDescription outputStream : outputStreams) {
			if (uniqueNames.contains(outputStream.getName())) {
				duplicatedNames.add(outputStream.getName());
			} else {
				uniqueNames.add(outputStream.getName());
			}
		}

		if (!duplicatedNames.isEmpty()) {
			throw new IllegalArgumentException("OutputStream should have unique name. Found duplicates : " + Lists.newArrayList(duplicatedNames));
		}
	}

	public void ensureNoDuplicatedInputStreams() {
		Set<String> duplicatedNames = new HashSet<>();
		Set<String> uniqueNames = new HashSet<>();

		for (InputStreamDescription inputStream : inputStreams) {
			if (uniqueNames.contains(inputStream.getName())) {
				duplicatedNames.add(inputStream.getName());
			} else {
				uniqueNames.add(inputStream.getName());
			}
		}

		if (!duplicatedNames.isEmpty()) {
			throw new IllegalArgumentException("InputStream should have unique name. Found duplicates : " + Lists.newArrayList(duplicatedNames));
		}
	}

	public void ensureNoDuplicatedProperties() {
		Set<String> duplicatedNames = new HashSet<>();
		Set<String> uniqueNames = new HashSet<>();

		for (PropertyDescription property : properties) {
			if (uniqueNames.contains(property.getName())) {
				duplicatedNames.add(property.getName());
			} else {
				uniqueNames.add(property.getName());
			}
		}

		if (!duplicatedNames.isEmpty()) {
			throw new IllegalArgumentException("Property should have unique name. Found duplicates : " + Lists.newArrayList(duplicatedNames));
		}
	}

	private void ensureInputStreamReference(String name) {
		boolean inputStreamExists = transform(inputStreams, new Function<InputStreamDescription, String>() {
			public String apply(InputStreamDescription description) {
				return description.getName();
			}
		}).contains(name);

		if (!inputStreamExists) {
			throw new IllegalArgumentException("Output stream reference a not existing input stream : \"" + name + "\"");
		}
	}

	public void ensureNoDuplicatedStateName() {
		Set<String> duplicatedNames = new HashSet<>();
		Set<String> uniqueNames = new HashSet<>();

		for (ReferencedStateDescription description : states) {
			if (uniqueNames.contains(description.getName())) {
				duplicatedNames.add(description.getName());
			} else {
				uniqueNames.add(description.getName());
			}
		}

		if (!duplicatedNames.isEmpty()) {
			throw new IllegalArgumentException("@State should have unique name. Found duplicates : " + Lists.newArrayList(duplicatedNames));
		}
	}

	public Class<? extends Component> getClazz() {
		return clazz;
	}

	public void setClazz(Class<? extends Component> clazz) {
		this.clazz = clazz;
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

	public ComponentType getType() {
		return type;
	}

	public void setType(ComponentType type) {
		this.type = type;
	}

	public List<PropertyDescription> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyDescription> properties) {
		this.properties = properties;
	}

	public List<OutputStreamDescription> getOutputStreams() {
		return outputStreams;
	}

	public void setOutputStreams(List<OutputStreamDescription> outputStreams) {
		this.outputStreams = outputStreams;
	}

	public List<InputStreamDescription> getInputStreams() {
		return inputStreams;
	}

	public void setInputStreams(List<InputStreamDescription> inputStreams) {
		this.inputStreams = inputStreams;
	}

	public List<ReferencedStateDescription> getStates() {
		return states;
	}

	public void setStates(List<ReferencedStateDescription> states) {
		this.states = states;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((inputStreams == null) ? 0 : inputStreams.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((outputStreams == null) ? 0 : outputStreams.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((states == null) ? 0 : states.hashCode());
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
		if (inputStreams == null) {
			if (other.inputStreams != null)
				return false;
		} else if (!inputStreams.equals(other.inputStreams))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (outputStreams == null) {
			if (other.outputStreams != null)
				return false;
		} else if (!outputStreams.equals(other.outputStreams))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (states == null) {
			if (other.states != null)
				return false;
		} else if (!states.equals(other.states))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ComponentDescription [clazz=" + clazz + ", name=" + name + ", description=" + description + ", type=" + type + ", properties=" + properties + ", outputStreams=" + outputStreams
				+ ", inputStreams=" + inputStreams + ", states=" + states + "]";
	}

}
