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
package stormy.pythian.component.preprocessor;

import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.instance.FeatureFunction;
import stormy.pythian.model.instance.FeatureProcedure;
import stormy.pythian.model.instance.Instance;

import com.google.common.util.concurrent.AtomicDouble;

@SuppressWarnings("serial")
@Documentation(name = "Normalizer")
public class Normalizer extends PreProcessor {

	@Override
	public Instance process(Instance original) {
		final AtomicDouble atomicMagnitude = new AtomicDouble(0.0);
		original.process(mapper, new FeatureProcedure<Double>() {
			@Override
			public void process(Double feature) {
				atomicMagnitude.getAndAdd(feature * feature);
			}
		});
		final double magnitude = Math.sqrt(atomicMagnitude.get());

		Instance newInstance = original.transform(mapper, new FeatureFunction<Double>() {
			@Override
			public Double transform(Double feature) {
				return feature / magnitude;
			}
		});

		return newInstance;
	}
}
