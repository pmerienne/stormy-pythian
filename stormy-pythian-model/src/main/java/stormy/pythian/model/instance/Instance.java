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
import java.util.Map;

import storm.trident.tuple.TridentTuple;

public class Instance implements Serializable {

	private static final long serialVersionUID = 4970738933759230736L;

	public final static String INSTANCE_FIELD = "INSTANCE_FIELD";
	public final static String NEW_INSTANCE_FIELD = "NEW_INSTANCE_FIELD";

	private final Object label;
	private final Object[] features;
	
	public static Instance from(TridentTuple tuple) {
		try {
			return (Instance) tuple.getValueByField(INSTANCE_FIELD);
		} catch (Exception ex) {
			throw new IllegalStateException("No instance found in tuple " + tuple, ex);
		}
	}

	public static Instance newInstance(OutputFeaturesMapper mapper, Map<String, Object> newFeaturesWithName) {
		Object[] newFeatures = new Object[mapper.size()];

		for (String featureName : newFeaturesWithName.keySet()) {
			int index = mapper.getFeatureIndex(featureName);
			if (index < 0) {
				throw new IllegalArgumentException("Feature " + featureName + " does not exist");
			}

			newFeatures[index] = newFeaturesWithName.get(featureName);

		}

		return new Instance(null, newFeatures);
	}

	public static Instance newInstance(OutputFeaturesMapper mapper, Object label, Map<String, Object> newFeaturesWithName) {
		Object[] newFeatures = new Object[mapper.size()];

		for (String featureName : newFeaturesWithName.keySet()) {
			int index = mapper.getFeatureIndex(featureName);
			if (index < 0) {
				throw new IllegalArgumentException("Feature " + featureName + " does not exist");
			}

			newFeatures[index] = newFeaturesWithName.get(featureName);

		}

		return new Instance(label, newFeatures);
	}

	Instance() {
		this.features = new Object[0];
		this.label = null;
	}

	Instance(Object label, int size) {
		this.label = label;
		this.features = new Object[size];
	}

	Instance(Object label, Object[] features) {
		this.label = label;
		this.features = features;
	}

	@SuppressWarnings("unchecked")
	public <T> T getFeature(InputFixedFeaturesMapper inputFixedFeaturesMapper, String featureName) {
		int index = inputFixedFeaturesMapper.getFeatureIndex(featureName);
		return (T) (index < 0 ? null : features[index]);
	}
	@SuppressWarnings("unchecked")
	public <T> T getFeature(OutputFeaturesMapper outputMapper, String featureName) {
		int index = outputMapper.getFeatureIndex(featureName);
		return (T) (index < 0 ? null : features[index]);
	}


	public <T> Instance withFeature(InputFixedFeaturesMapper inputFixedFeaturesMapper, String featureName, T feature) {
		int index = inputFixedFeaturesMapper.getFeatureIndex(featureName);
		if (index < 0) {
			throw new IllegalArgumentException("Feature " + featureName + " does not exist");
		}

		Object[] newFeatures = new Object[features.length];
		System.arraycopy(features, 0, newFeatures, 0, features.length);

		newFeatures[index] = feature;

		return new Instance(this.label, newFeatures);
	}

	public Instance withFeatures(InputFixedFeaturesMapper inputFixedFeaturesMapper, Map<String, Object> newFeaturesWithName) {
		Object[] newFeatures = new Object[features.length];
		System.arraycopy(features, 0, newFeatures, 0, features.length);

		for (String featureName : newFeaturesWithName.keySet()) {
			int index = inputFixedFeaturesMapper.getFeatureIndex(featureName);
			if (index < 0) {
				throw new IllegalArgumentException("Feature " + featureName + " does not exist");
			}

			newFeatures[index] = newFeaturesWithName.get(featureName);

		}

		return new Instance(this.label, newFeatures);
	}

	public <T> Instance withFeature(OutputFeaturesMapper mapper, String featureName, T feature) {
		int index = mapper.getFeatureIndex(featureName);
		if (index < 0) {
			throw new IllegalArgumentException("Feature " + featureName + " does not exist");
		}

		Object[] newFeatures = new Object[mapper.size()];
		System.arraycopy(features, 0, newFeatures, 0, features.length);

		newFeatures[index] = feature;

		return new Instance(this.label, newFeatures);
	}

	public Instance withFeatures(OutputFeaturesMapper mapper, Map<String, Object> newFeaturesWithName) {
		Object[] newFeatures = new Object[mapper.size()];
		System.arraycopy(features, 0, newFeatures, 0, features.length);

		for (String featureName : newFeaturesWithName.keySet()) {
			int index = mapper.getFeatureIndex(featureName);
			if (index < 0) {
				throw new IllegalArgumentException("Feature " + featureName + " does not exist");
			}

			newFeatures[index] = newFeaturesWithName.get(featureName);

		}

		return new Instance(this.label, newFeatures);
	}
	
	public Instance withLabel(Object label) {
		Object[] newFeatures = new Object[features.length];
		System.arraycopy(features, 0, newFeatures, 0, features.length);
		return new Instance(this.label, newFeatures);
	}

	public Object[] getSelectedFeatures(InputUserSelectionFeaturesMapper inputUserSelectionFeaturesMapper) {
		int[] selectedIndex = inputUserSelectionFeaturesMapper.getSelectedIndex();

		Object[] selectedFeatures = new Object[selectedIndex.length];
		int i = 0;
		for (int index : selectedIndex) {
			selectedFeatures[i] = features[index];
			i++;
		}

		return selectedFeatures;
	}

	@SuppressWarnings("unchecked")
	public <T> void process(InputUserSelectionFeaturesMapper inputUserSelectionFeaturesMapper, FeatureProcedure<T> featureProcedure) {
		int[] selectedIndex = inputUserSelectionFeaturesMapper.getSelectedIndex();
		for (int index : selectedIndex) {
			featureProcedure.process((T) features[index]);
		}

	}

	@SuppressWarnings("unchecked")
	public <T> Instance transform(InputUserSelectionFeaturesMapper inputUserSelectionFeaturesMapper, FeatureFunction<T> function) {
		Object[] newFeatures = new Object[features.length];
		System.arraycopy(features, 0, newFeatures, 0, features.length);

		int[] selectedIndex = inputUserSelectionFeaturesMapper.getSelectedIndex();
		for (int index : selectedIndex) {
			newFeatures[index] = function.transform((T) features[index]);
		}

		return new Instance(this.label, newFeatures);
	}

	public int size() {
		return this.features.length;
	}
	
	public boolean hasLabel() {
		return this.label != null;
	}

	public Object getLabel() {
		return this.label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(features);
		result = prime * result + ((label == null) ? 0 : label.hashCode());
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
		if (!Arrays.equals(features, other.features))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Instance [label=" + label + ", features=" + Arrays.toString(features) + "]";
	}
	
	

}
