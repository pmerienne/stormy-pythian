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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FeaturesIndex implements Serializable {

	private static final long serialVersionUID = 4675835465674004328L;

	private final Map<String, Integer> mapping;

	public FeaturesIndex() {
		this.mapping = new HashMap<>();
	}

	public FeaturesIndex(Map<String, Integer> mapping) {
		this.mapping = mapping;
	}

	public FeaturesIndex(Collection<String> features) {
		this.mapping = new HashMap<>(features.size());
		for (String feature : features) {
			this.mapping.put(feature, mapping.size());
		}
	}

	public int getIndex(String featureName) {
		Integer index = mapping.get(featureName);
		return index == null ? -1 : index;
	}

	public int size() {
		return mapping.size();
	}

	@Override
	public String toString() {
		return "FeaturesIndex [mapping=" + mapping + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mapping == null) ? 0 : mapping.hashCode());
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
		FeaturesIndex other = (FeaturesIndex) obj;
		if (mapping == null) {
			if (other.mapping != null)
				return false;
		} else if (!mapping.equals(other.mapping))
			return false;
		return true;
	}

	public static class Builder {

		private final Map<String, Integer> mapping;

		public Builder() {
			mapping = new HashMap<>();
		}

		public Builder(Map<String, Integer> mapping) {
			this.mapping = new HashMap<>(mapping);
		}

		public static Builder featuresIndex() {
			return new Builder();
		}

		public static Builder from(FeaturesIndex index) {
			return new Builder(index.mapping);
		}

		public Builder with(String feature) {
			mapping.put(feature, mapping.size());
			return this;
		}

		public Builder with(Collection<String> features) {
			for (String feature : features) {
				with(feature);
			}
			return this;
		}

		public FeaturesIndex build() {
			return new FeaturesIndex(mapping);
		}
	}

}
