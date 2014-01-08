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
package stormy.pythian.model.instance;

import static stormy.pythian.model.annotation.MappingType.FIXED_FEATURES;

import java.util.HashMap;
import java.util.Map;

import stormy.pythian.model.annotation.MappingType;

public class FixedFeaturesMapper implements FeaturesMapper {

	private static final long serialVersionUID = -1845403070125797936L;

	private final Map<String, String> mappings;

	public FixedFeaturesMapper(Map<String, String> mappings) {
		this.mappings = mappings;
	}

	public <T> Feature<T> getFeature(Instance instance, String featureName) {
		String outsideName = mappings.get(featureName);
		if (outsideName != null) {
			return instance.get(outsideName);
		} else {
			return null;
		}
	}

	public <T> void setFeature(Instance instance, String featureName, Feature<T> feature) {
		String outsideName = mappings.get(featureName);
		instance.add(outsideName, feature);
	}

	@Override
	public Map<String, Feature<?>> getFeatures(Instance instance) {
		Map<String, Feature<?>> features = new HashMap<>(mappings.size());

		for (String featureName : mappings.keySet()) {
			String outsideName = mappings.get(featureName);
			features.put(outsideName, instance.get(outsideName));

		}

		return features;
	}

	@Override
	public MappingType getType() {
		return FIXED_FEATURES;
	}

	public Map<String, String> getMappings() {
		return mappings;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mappings == null) ? 0 : mappings.hashCode());
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
		FixedFeaturesMapper other = (FixedFeaturesMapper) obj;
		if (mappings == null) {
			if (other.mappings != null)
				return false;
		} else if (!mappings.equals(other.mappings))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FixedFeaturesMapper [mappings=" + mappings + "]";
	}

}
