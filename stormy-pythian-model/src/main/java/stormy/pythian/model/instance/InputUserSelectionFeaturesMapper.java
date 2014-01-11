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
import java.util.List;

public class InputUserSelectionFeaturesMapper implements Serializable {

	private static final long serialVersionUID = 3749997614862014103L;

	private FeaturesIndex featuresIndex;
	private int[] selectedIndex;

	public InputUserSelectionFeaturesMapper(FeaturesIndex featuresIndex, List<String> selectedFeatures) {
		this.featuresIndex = featuresIndex;
		this.selectedIndex = new int[selectedFeatures.size()];

		for (int i = 0; i < selectedFeatures.size(); i++) {
			int selectedIndex = featuresIndex.getIndex(selectedFeatures.get(i));
			this.selectedIndex[i] = selectedIndex;
		}
	}

	public int[] getSelectedIndex() {
		return selectedIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((featuresIndex == null) ? 0 : featuresIndex.hashCode());
		result = prime * result + Arrays.hashCode(selectedIndex);
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
		InputUserSelectionFeaturesMapper other = (InputUserSelectionFeaturesMapper) obj;
		if (featuresIndex == null) {
			if (other.featuresIndex != null)
				return false;
		} else if (!featuresIndex.equals(other.featuresIndex))
			return false;
		if (!Arrays.equals(selectedIndex, other.selectedIndex))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InputUserSelectionFeaturesMapper [featuresIndex=" + featuresIndex + ", selectedIndex=" + Arrays.toString(selectedIndex) + "]";
	}

}
