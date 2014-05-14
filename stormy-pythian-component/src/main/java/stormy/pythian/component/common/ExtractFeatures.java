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
import java.util.ArrayList;
import java.util.List;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.instance.Feature;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.ListedFeaturesMapper;
import stormy.pythian.model.instance.NamedFeaturesMapper;
import backtype.storm.tuple.Values;

public class ExtractFeatures extends BaseFunction {

    private static final long serialVersionUID = -2823417821288444544L;

    private final ListedFeaturesMapper listedFeaturesMapper;
    private final NamedFeaturesMapper namedFeaturesMapper;

    private final String[] featureNames;

    public ExtractFeatures(NamedFeaturesMapper mapper, String... featureNames) {
        checkNotNull(mapper);
        checkNotNull(featureNames);

        this.listedFeaturesMapper = null;
        this.namedFeaturesMapper = mapper;
        this.featureNames = featureNames;
    }

    public ExtractFeatures(ListedFeaturesMapper userSelectionFeaturesMapper) {
        checkNotNull(userSelectionFeaturesMapper);

        this.listedFeaturesMapper = userSelectionFeaturesMapper;
        this.namedFeaturesMapper = null;
        this.featureNames = null;
    }

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        List<Object> features = null;

        if (listedFeaturesMapper != null) {
            Instance instance = Instance.get(tuple, listedFeaturesMapper);
            features = new ArrayList<>(instance.size());
            for (Feature<?> feature : instance.getFeatures()) {
                features.add(feature.getValue());
            }
        } else if (namedFeaturesMapper != null) {
            Instance instance = Instance.get(tuple, namedFeaturesMapper);

            features = new ArrayList<>(featureNames.length);
            for (String featureName : featureNames) {
                features.add(instance.getFeature(featureName).getValue());
            }
        }

        collector.emit(new Values(features.toArray()));
    }

}
