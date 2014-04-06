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

import static stormy.pythian.model.annotation.ComponentType.LEARNER;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.Property;

import com.github.pmerienne.trident.ml.classification.PerceptronClassifier;

@SuppressWarnings("serial")
@Documentation(name = "Perceptron classifier", description = "Perceptron classifier from trident-ml", type = LEARNER)
public class TridentMLPerceptronClassifier extends TridentMLClassifier<Boolean> {

	@Property(name = "Bias")
	private Double bias = 0.0;

	@Property(name = "Threshold")
	private Double threshold = 0.5;

	@Property(name = "Learning rate")
	private Double learningRate = 0.1;

	@Override
	public void initClassifier() {
		this.classifier = new PerceptronClassifier(bias, threshold, learningRate);
	}

}
