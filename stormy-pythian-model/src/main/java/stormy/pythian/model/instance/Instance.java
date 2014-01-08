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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import storm.trident.tuple.TridentTuple;

public class Instance implements Serializable {

	private static final long serialVersionUID = 4970738933759230736L;

	public final static String INSTANCE_FIELD = "INSTANCE_FIELD";
	public final static String NEW_INSTANCE_FIELD = "NEW_INSTANCE_FIELD";

	private final Map<String, Feature<?>> features;

	public Instance() {
		this.features = new HashMap<>();
	}

	public Instance(Instance original) {
		this.features = new HashMap<>(original.features);
	}

	public void add(String name, Feature<?> feature) {
		this.features.put(name, feature);
	}

	public void add(String name, String value) {
		this.features.put(name, new TextFeature(value));
	}

	public void add(String name, Integer value) {
		this.features.put(name, new IntegerFeature(value));
	}

	public void add(String name, Double value) {
		this.features.put(name, new DoubleFeature(value));
	}

	@SuppressWarnings("unchecked")
	public <T> Feature<T> get(String name) {
		return (Feature<T>) this.features.get(name);
	}

	public int size() {
		return this.features.size();
	}

	public Set<String> featureNames() {
		return this.features.keySet();
	}

	public static Instance from(TridentTuple tuple) {
		Instance instance;
		try {
			instance = (Instance) tuple.getValueByField(INSTANCE_FIELD);
		} catch (Exception ex) {
			instance = null;
		}
		return instance;
	}

	public Map<String, Feature<?>> getFeatures() {
		return features;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((features == null) ? 0 : features.hashCode());
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
		Instance other = (Instance) obj;
		if (features == null) {
			if (other.features != null)
				return false;
		} else if (!features.equals(other.features))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Instance [features=" + features + "]";
	}

	public static class Builder {

		private final Map<String, Feature<?>> features = new HashMap<>();

		public static Builder instance() {
			return new Builder();
		}

		public Builder with(String name, Feature<?> feature) {
			this.features.put(name, feature);
			return this;
		}

		public Builder with(String name, String value) {
			this.features.put(name, new TextFeature(value));
			return this;
		}

		public Builder with(String name, Integer value) {
			this.features.put(name, new IntegerFeature(value));
			return this;
		}

		public Builder with(String name, Double value) {
			this.features.put(name, new DoubleFeature(value));
			return this;
		}

		public Instance build() {
			Instance instance = new Instance();
			for (String featureName : features.keySet()) {
				instance.add(featureName, features.get(featureName));
			}
			return instance;
		}
	}
}
