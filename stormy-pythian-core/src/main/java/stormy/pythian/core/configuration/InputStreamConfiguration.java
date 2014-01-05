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
import java.util.Map;

import stormy.pythian.core.description.InputStreamDescription;
import stormy.pythian.model.annotation.MappingType;

public class InputStreamConfiguration {

	private final InputStreamDescription descriptor;

	private final Map<String, String> mappings;
	private final List<String> selectedFeatures;

	public InputStreamConfiguration(InputStreamDescription descriptor, List<String> selectedFeatures) {
		this.descriptor = descriptor;
		this.selectedFeatures = selectedFeatures;
		this.mappings = null;
	}

	public InputStreamConfiguration(InputStreamDescription descriptor, Map<String, String> mappings) {
		this.descriptor = descriptor;
		this.mappings = mappings;
		this.selectedFeatures = null;
	}

	public InputStreamDescription getDescriptor() {
		return descriptor;
	}

	public Map<String, String> getMappings() {
		return mappings;
	}

	public List<String> getSelectedFeatures() {
		return selectedFeatures;
	}

	public String getStreamName() {
		return descriptor.getName();
	}

	public MappingType getMappingType() {
		return descriptor.getType();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((descriptor == null) ? 0 : descriptor.hashCode());
		result = prime * result + ((mappings == null) ? 0 : mappings.hashCode());
		result = prime * result + ((selectedFeatures == null) ? 0 : selectedFeatures.hashCode());
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
		InputStreamConfiguration other = (InputStreamConfiguration) obj;
		if (descriptor == null) {
			if (other.descriptor != null)
				return false;
		} else if (!descriptor.equals(other.descriptor))
			return false;
		if (mappings == null) {
			if (other.mappings != null)
				return false;
		} else if (!mappings.equals(other.mappings))
			return false;
		if (selectedFeatures == null) {
			if (other.selectedFeatures != null)
				return false;
		} else if (!selectedFeatures.equals(other.selectedFeatures))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InputStreamConfiguration [descriptor=" + descriptor + ", mappings=" + mappings + ", selectedFeatures=" + selectedFeatures + "]";
	}

}
