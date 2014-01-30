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

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFixedFeaturesMapper;
import backtype.storm.tuple.Values;

public class AddFeature extends BaseFunction {

	private static final long serialVersionUID = 1L;

	private final OutputFixedFeaturesMapper predictionOutputMapper;
	private final String fieldName;
	private final String featureName;

	public AddFeature(OutputFixedFeaturesMapper predictionOutputMapper, String fieldName, String featureName) {
		this.predictionOutputMapper = predictionOutputMapper;
		this.fieldName = fieldName;
		this.featureName = featureName;
	}

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		Instance original = Instance.from(tuple);

		Object newFeature = tuple.getValueByField(fieldName);
		Instance updatedInstance = original.withFeature(predictionOutputMapper, featureName, newFeature);
		collector.emit(new Values(updatedInstance));
	}

}