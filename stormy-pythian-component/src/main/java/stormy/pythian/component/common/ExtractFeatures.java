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
package stormy.pythian.component.common;

import static com.google.common.base.Preconditions.checkNotNull;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.instance.InputFixedFeaturesMapper;
import stormy.pythian.model.instance.InputUserSelectionFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import backtype.storm.tuple.Values;

public class ExtractFeatures extends BaseFunction {

	private static final long serialVersionUID = -2823417821288444544L;

	private final InputUserSelectionFeaturesMapper userSelectionFeaturesMapper;

	private final InputFixedFeaturesMapper fixedFeaturesMapper;
	private final String[] featureNames;

	public ExtractFeatures(InputFixedFeaturesMapper mapper, String... featureNames) {
		checkNotNull(mapper);
		checkNotNull(featureNames);

		this.userSelectionFeaturesMapper = null;
		this.fixedFeaturesMapper = mapper;
		this.featureNames = featureNames;
	}

	public ExtractFeatures(InputUserSelectionFeaturesMapper userSelectionFeaturesMapper) {
		checkNotNull(userSelectionFeaturesMapper);

		this.userSelectionFeaturesMapper = userSelectionFeaturesMapper;
		this.fixedFeaturesMapper = null;
		this.featureNames = null;
	}

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		Instance instance = Instance.from(tuple);
		Object[] features = getFeatures(instance);
		collector.emit(new Values(features));
	}

	private Object[] getFeatures(Instance instance) {
		if (userSelectionFeaturesMapper != null) {
			Object[] selectedFeatures = instance.getSelectedFeatures(userSelectionFeaturesMapper);
			return selectedFeatures;
		} else if (fixedFeaturesMapper != null && featureNames != null) {
			Object[] features = new Object[featureNames.length];

			for (int i = 0; i < featureNames.length; i++) {
				String featureName = featureNames[i];
				features[i] = instance.getFeature(fixedFeaturesMapper, featureName);
			}

			return features;
		} else {
			throw new IllegalStateException("Either userSelectionFeaturesMapper or fixedFeaturesMapper should be set");
		}
	}

}