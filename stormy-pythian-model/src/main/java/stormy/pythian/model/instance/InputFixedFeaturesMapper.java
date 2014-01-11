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
import java.util.Map;

public class InputFixedFeaturesMapper implements Serializable {

	private static final long serialVersionUID = 5298891914881030970L;

	private Map<String, String> mappings;
	private FeaturesIndex featuresIndex;

	public InputFixedFeaturesMapper(FeaturesIndex featuresIndex, Map<String, String> mappings) {
		this.featuresIndex = featuresIndex;
		this.mappings = mappings;
	}

	public int getFeatureIndex(String featureName) {
		String outsideName = mappings.get(featureName);
		if (outsideName != null) {
			int index = featuresIndex.getIndex(outsideName);
			return index;
		} else {
			return -1;
		}
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
		InputFixedFeaturesMapper other = (InputFixedFeaturesMapper) obj;
		if (mappings == null) {
			if (other.mappings != null)
				return false;
		} else if (!mappings.equals(other.mappings))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InputFixedFeaturesMapper [mappings=" + mappings + ", featuresIndex=" + featuresIndex + "]";
	}

}
