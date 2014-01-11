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

public class OutputFeaturesMapper implements Serializable {

	private static final long serialVersionUID = -1845403070125797936L;

	private final Map<String, String> mappings;

	private final FeaturesIndex featuresIndex;

	public OutputFeaturesMapper(FeaturesIndex index, Map<String, String> mappings) {
		this.featuresIndex = index;
		this.mappings = mappings;
	}

	public InstanceView from(Instance original) {
		return new InstanceView(original, mappings, featuresIndex);
	}

	public InstanceView newInstance() {
		return new InstanceView(mappings, featuresIndex);
	}

	public static class InstanceView {

		private final Instance instance;

		private final Map<String, String> mappings;
		private final FeaturesIndex featuresIndex;

		public InstanceView(Instance original, Map<String, String> mappings, FeaturesIndex featuresIndex) {
			this.mappings = mappings;
			this.featuresIndex = featuresIndex;

			Feature<?>[] originalFeatures = original.getFeatures();
			Feature<?>[] features = new Feature<?>[featuresIndex.size()];
			System.arraycopy(originalFeatures, 0, features, 0, originalFeatures.length);

			this.instance = new Instance(features);
		}

		public InstanceView(Map<String, String> mappings, FeaturesIndex featuresIndex) {
			this.mappings = mappings;
			this.featuresIndex = featuresIndex;

			Feature<?>[] features = new Feature<?>[featuresIndex.size()];
			this.instance = new Instance(features);
		}

		public InstanceView add(String featureName, Feature<?> feature) {
			String outsideName = mappings.get(featureName);
			if (outsideName != null) {
				int index = featuresIndex.getIndex(outsideName);
				if (index >= 0) {
					instance.getFeatures()[index] = feature;
				} else {
					throw new IllegalArgumentException("Instance does not contain feature " + featureName);
				}
			} else {
				throw new IllegalArgumentException("No mappings found for feature " + featureName);
			}

			return this;
		}

		public Instance build() {
			return instance;
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
		OutputFeaturesMapper other = (OutputFeaturesMapper) obj;
		if (mappings == null) {
			if (other.mappings != null)
				return false;
		} else if (!mappings.equals(other.mappings))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OutputFeaturesMapper [mappings=" + mappings + "]";
	}

}
