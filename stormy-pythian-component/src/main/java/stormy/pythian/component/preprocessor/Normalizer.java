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

import static java.lang.Math.sqrt;
import java.util.List;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.Instance.FeatureProcessor;

@SuppressWarnings("serial")
@Documentation(name = "Normalizer")
public class Normalizer extends PreProcessor {

    @Override
    public Instance process(Instance instance) {
        List<Double> features = instance.getFeatures();

        double magnitude = 0;
        for (Double feature : features) {
            magnitude += feature * feature;
        }
        
        final double realMagnitude = sqrt(magnitude);
        instance.process(new FeatureProcessor<Double>() {
            @Override
            public Double process(Double feature) {
                return feature / realMagnitude;
            }
        });

        return instance;
    }
}
