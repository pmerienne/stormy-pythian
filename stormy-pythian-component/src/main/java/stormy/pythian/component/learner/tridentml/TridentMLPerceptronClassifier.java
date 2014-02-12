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
package stormy.pythian.component.learner.tridentml;

import stormy.pythian.model.annotation.Property;

import com.github.pmerienne.trident.ml.classification.Classifier;
import com.github.pmerienne.trident.ml.classification.PerceptronClassifier;

public class TridentMLPerceptronClassifier extends TridentMLClassifier<Boolean> {

	private static final long serialVersionUID = 1L;

	@Property(name = "Bias")
	public double bias = 0.0;

	@Property(name = "Threshold")
	public double threshold = 0.5;

	@Property(name = "Learning rate")
	public double learningRate = 0.1;

	@Override
	public void init() {
		Classifier<Boolean> classifier = new PerceptronClassifier(bias, threshold, learningRate);
		initClassifierStreams(classifier);
	}

}