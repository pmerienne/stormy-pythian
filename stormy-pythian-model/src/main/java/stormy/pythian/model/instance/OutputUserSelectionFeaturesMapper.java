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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

public class OutputUserSelectionFeaturesMapper implements Serializable {

	private static final long serialVersionUID = 6993554834452684410L;

	private final FeaturesIndex featuresIndex;
	private final int[] newFeatureIndexes;

	public OutputUserSelectionFeaturesMapper(FeaturesIndex featuresIndex, Collection<String> newFeatures) {
		this.featuresIndex = featuresIndex;
		this.newFeatureIndexes = new int[newFeatures.size()];
		
		int i = 0;
		for (String newFeature : newFeatures) {
			int selectedIndex = featuresIndex.getIndex(newFeature);
			this.newFeatureIndexes[i] = selectedIndex;
			i++;
		}
	}

	public int size() {
		return featuresIndex.size();
	}

	public int[] getNewFeatureIndexes() {
		return newFeatureIndexes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((featuresIndex == null) ? 0 : featuresIndex.hashCode());
		result = prime * result + Arrays.hashCode(newFeatureIndexes);
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
		OutputUserSelectionFeaturesMapper other = (OutputUserSelectionFeaturesMapper) obj;
		if (featuresIndex == null) {
			if (other.featuresIndex != null)
				return false;
		} else if (!featuresIndex.equals(other.featuresIndex))
			return false;
		if (!Arrays.equals(newFeatureIndexes, other.newFeatureIndexes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OutputUserSelectionFeaturesMapper [featuresIndex=" + featuresIndex + ", newFeatureIndexes=" + Arrays.toString(newFeatureIndexes) + "]";
	}

}
