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
package stormy.pythian.component.classifier.tridentml;

import com.github.pmerienne.trident.ml.classification.Classifier;

@SuppressWarnings("serial")
public abstract class TridentMLClassifier<L> extends stormy.pythian.component.classifier.Classifier<L> {

	protected Classifier<L> classifier;

	@Override
	protected void update(L label, Object[] rawFeatures) {
		double[] features = new double[rawFeatures.length];
		for (int i = 0; i < rawFeatures.length; i++) {
			if (rawFeatures[i] instanceof Number) {
				features[i] = ((Number) rawFeatures[i]).doubleValue();
			} else {
				features[i] = 0.0;
			}
		}

		this.classifier.update(label, features);
	}

	@Override
	protected L classify(Object[] rawFeatures) {
		double[] features = new double[rawFeatures.length];
		for (int i = 0; i < rawFeatures.length; i++) {
			if (rawFeatures[i] instanceof Number) {
				features[i] = ((Number) rawFeatures[i]).doubleValue();
			} else {
				features[i] = 0.0;
			}
		}

		L prediction = this.classifier.classify(features);
		return prediction;
	}
}
