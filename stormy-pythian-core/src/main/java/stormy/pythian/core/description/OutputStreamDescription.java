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

import static org.apache.commons.lang.StringUtils.isBlank;
import static stormy.pythian.model.annotation.MappingType.FIXED_FEATURES;
import static stormy.pythian.model.annotation.MappingType.USER_SELECTION;

import java.util.List;

import stormy.pythian.model.annotation.MappingType;

public class OutputStreamDescription {

	private final String name;
	private final String from;
	private final List<FeatureDescription> newFeatures;

	private final MappingType mappingType;

	public OutputStreamDescription(String name, String from, List<FeatureDescription> newFeatures) {
		this.name = name;
		this.from = isBlank(from) ? null : from;
		this.newFeatures = newFeatures;
		this.mappingType = FIXED_FEATURES;
	}

	public OutputStreamDescription(String name, String from) {
		this.name = name;
		this.from = isBlank(from) ? null : from;
		this.newFeatures = null;
		this.mappingType = USER_SELECTION;
	}

	public OutputStreamDescription(String name) {
		this.name = name;
		this.from = null;
		this.newFeatures = null;
		this.mappingType = USER_SELECTION;
	}

	public OutputStreamDescription(String name, List<FeatureDescription> newFeatures) {
		this.name = name;
		this.from = null;
		this.newFeatures = newFeatures;
		this.mappingType = FIXED_FEATURES;
	}

	public String getName() {
		return name;
	}

	public String getFrom() {
		return from;
	}

	public List<FeatureDescription> getNewFeatures() {
		return newFeatures;
	}

	public MappingType getMappingType() {
		return mappingType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((isBlank(from)) ? 0 : from.hashCode());
		result = prime * result + ((mappingType == null) ? 0 : mappingType.hashCode());
		result = prime * result + ((isBlank(name)) ? 0 : name.hashCode());
		result = prime * result + ((newFeatures == null) ? 0 : newFeatures.hashCode());
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
		OutputStreamDescription other = (OutputStreamDescription) obj;
		if (isBlank(from)) {
			if (!isBlank(other.from))
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (mappingType != other.mappingType)
			return false;
		if (isBlank(name)) {
			if (!isBlank(other.name))
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (newFeatures == null) {
			if (other.newFeatures != null)
				return false;
		} else if (!newFeatures.equals(other.newFeatures))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OutputStreamDescription [name=" + name + ", from=" + from + ", newFeatures=" + newFeatures + ", mappingType=" + mappingType + "]";
	}

}
