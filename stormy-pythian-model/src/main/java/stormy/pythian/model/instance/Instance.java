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
import java.util.ArrayList;
import java.util.List;

import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.instance.Instance.Builder;

public class Instance implements Serializable {

	private static final long serialVersionUID = 4970738933759230736L;

	public final static String INSTANCE_FIELD = "INSTANCE_FIELD";
	public final static String NEW_INSTANCE_FIELD = "NEW_INSTANCE_FIELD";

	private final Feature<?>[] features;

	Instance() {
		this.features = new Feature<?>[0];
	}

	Instance(int size) {
		this.features = new Feature<?>[size];
	}

	Instance(Feature<?>[] features) {
		this.features = features;
	}

	public Feature<?>[] getFeatures() {
		return features;
	}

	public Feature<?> get(int i) {
		return this.features[i];
	}

	public void set(int i, Feature<?> feature) {
		this.features[i] = feature;
	}

	public int size() {
		return this.features.length;
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

		private final List<Feature<?>> features = new ArrayList<>();

		public static Builder instance() {
			return new Builder();
		}

		public Builder with(Feature<?> feature) {
			this.features.add(feature);
			return this;
		}

		public Builder with(String string) {
			return with(new TextFeature(string));
		}
		
		public Instance build() {
			Instance instance = new Instance(features.size());
			int i = 0;
			for (Feature<?> feature : features) {
				instance.set(i, feature);
				i++;
			}
			return instance;
		}

	}

	public static Instance from(TridentTuple tuple) {
		try {
			return (Instance) tuple.getValueByField(INSTANCE_FIELD);
		} catch (Exception ex) {
			throw new IllegalStateException("No instance found in tuple " + tuple, ex);
		}
	}
}
