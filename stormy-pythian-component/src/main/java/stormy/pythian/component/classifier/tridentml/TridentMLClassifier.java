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

import java.util.List;
import com.github.pmerienne.trident.ml.classification.Classifier;

@SuppressWarnings("serial")
public abstract class TridentMLClassifier<L> extends stormy.pythian.component.classifier.Classifier<L> {

    protected Classifier<L> classifier;

    @Override
    protected void update(L label, List<Object> rawFeatures) {
        double[] features = toFeatureArray(rawFeatures);
        this.classifier.update(label, features);
    }

    @Override
    protected L classify(List<Object> rawFeatures) {
        double[] features = toFeatureArray(rawFeatures);
        L prediction = this.classifier.classify(features);
        return prediction;
    }

    private double[] toFeatureArray(List<Object> rawFeatures) {
        double[] features = new double[rawFeatures.size()];

        for (int i = 0; i < rawFeatures.size(); i++) {
            Object rawFeature = rawFeatures.get(i);
            if (rawFeature instanceof Number) {
                features[i] = ((Number) rawFeature).doubleValue();
            } else {
                try {
                    features[i] = Double.parseDouble(rawFeature.toString());
                } catch (Exception e) {
                    features[i] = 0.0;
                }
            }
        }

        return features;
    }
}
