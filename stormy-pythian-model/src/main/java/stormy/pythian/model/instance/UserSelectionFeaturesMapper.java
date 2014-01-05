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

import static stormy.pythian.model.annotation.MappingType.USER_SELECTION;

import java.util.ArrayList;
import java.util.List;

import stormy.pythian.model.annotation.MappingType;

public class UserSelectionFeaturesMapper implements FeaturesMapper {

	private static final long serialVersionUID = 3749997614862014103L;

	private final List<String> selectedFeatures;

	public UserSelectionFeaturesMapper() {
		this.selectedFeatures = new ArrayList<>();
	}

	public UserSelectionFeaturesMapper(List<String> selectedFeatures) {
		this.selectedFeatures = selectedFeatures;
	}

	@Override
	public List<Feature<?>> getFeatures(Instance instance) {
		List<Feature<?>> features = new ArrayList<>(selectedFeatures.size());

		for (String featureName : selectedFeatures) {
			Feature<?> feature = instance.get(featureName);
			features.add(feature);
		}

		return features;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Feature<T> getFeature(Instance instance, String featureName) {
		return (Feature<T>) (selectedFeatures.contains(featureName) ? instance.get(featureName) : null);
	}

	@Override
	public MappingType getType() {
		return USER_SELECTION;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		UserSelectionFeaturesMapper other = (UserSelectionFeaturesMapper) obj;
		if (selectedFeatures == null) {
			if (other.selectedFeatures != null)
				return false;
		} else if (!selectedFeatures.equals(other.selectedFeatures))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserSelectionFeaturesMapper [selectedFeatures=" + selectedFeatures + "]";
	}

}
