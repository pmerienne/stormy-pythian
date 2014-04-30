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
import stormy.pythian.model.instance.Feature;
import stormy.pythian.model.instance.Instance;
import com.github.pmerienne.trident.ml.classification.Classifier;

@SuppressWarnings("serial")
public abstract class TridentMLClassifier<L> extends stormy.pythian.component.classifier.Classifier<L> {

    protected Classifier<L> classifier;

    @Override
    protected void update(Instance instance) {
        double[] features = extractDoubleFeatures(instance);
        this.classifier.update(getLabel(instance), features);
    }

    @Override
    protected void classify(Instance instance) {
        double[] features = extractDoubleFeatures(instance);
        L prediction = this.classifier.classify(features);
        this.setLabel(instance, prediction);
    }

    private double[] extractDoubleFeatures(Instance instance) {
        double[] features = new double[instance.size()];

        List<Feature<?>> rawFeatures = instance.getFeatures();
        for (int i = 0; i < rawFeatures.size(); i++) {
            features[i] = rawFeatures.get(i).decimalValue();
        }

        return features;
    }

    protected abstract void setLabel(Instance instance, L label);

    protected abstract L getLabel(Instance instance);

}
