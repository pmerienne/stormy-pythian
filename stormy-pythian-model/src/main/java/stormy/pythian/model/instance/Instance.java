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
package stormy.pythian.model.instance;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.base.Preconditions;
import storm.trident.tuple.TridentTuple;

public class Instance implements Serializable {

    private static final long serialVersionUID = 4970738933759230736L;

    public final static String INSTANCE_FIELD = "INSTANCE_FIELD";
    public final static String NEW_INSTANCE_FIELD = "NEW_INSTANCE_FIELD";

    private transient ListedFeaturesMapper inputListedFeaturesMapper;
    private transient NamedFeaturesMapper inputNamedFeaturesMapper;
    private transient ListedFeaturesMapper outputListedFeaturesMapper;
    private transient NamedFeaturesMapper outputNamedFeaturesMapper;

    Object label;
    Map<String, Object> features;

    public Instance() {
        this.label = null;
        this.features = new HashMap<>();
    }

    public int size() {
        return this.features.size();
    }

    public boolean hasLabel() {
        return this.label != null;
    }

    public Object getLabel() {
        return this.label;
    }

    public void setLabel(Object label) {
        this.label = label;
    }

    @SuppressWarnings("unchecked")
    public <T> T getFeature(String featureName) {
        checkNotNull(featureName, "Feature name is mandatory");
        checkNotNull(inputNamedFeaturesMapper, "Cannot get feature : no input named features mapper");

        String realFeatureName = inputNamedFeaturesMapper.getFeatureName(featureName);
        return (T) features.get(realFeatureName);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getFeatures() {
        checkNotNull(inputListedFeaturesMapper, "Cannot get features : no input listed features mapper");
        List<T> features = new ArrayList<>(inputListedFeaturesMapper.size());

        List<String> selectedFeatureNames = inputListedFeaturesMapper.getSelectedFeatures();
        for (String selectedFeatureName : selectedFeatureNames) {
            features.add((T) this.features.get(selectedFeatureName));
        }

        return features;
    }

    public <T> void setFeature(String featureName, T feature) {
        checkNotNull(outputNamedFeaturesMapper, "Cannot set feature : no output named features mapper");
        String realFeatureName = outputNamedFeaturesMapper.getFeatureName(featureName);
        features.put(realFeatureName, feature);
    }

    public <T> void setFeatures(Map<String, T> features) {
        for (String featureName : features.keySet()) {
            setFeature(featureName, features.get(featureName));
        }
    }

    public <T> void addFeatures(List<T> newFeatures) {
        checkNotNull(outputListedFeaturesMapper, "Cannot set features : no output listed features mapper");
        Preconditions.checkArgument(
                outputListedFeaturesMapper.size() == newFeatures.size(),
                "Cannot add features, expecting %s new features, got %s", outputListedFeaturesMapper.size(), newFeatures.size());

        List<String> selection = outputListedFeaturesMapper.getSelectedFeatures();
        for (int i = 0; i < selection.size(); i++) {
            this.features.put(selection.get(i), newFeatures.get(i));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void process(FeatureProcessor<T> processor) {
        checkNotNull(inputListedFeaturesMapper, "Cannot process features : no intput listed features mapper");
        List<String> selectedFeatureNames = inputListedFeaturesMapper.getSelectedFeatures();

        for (String featureName : selectedFeatureNames) {
            T feature = (T) this.features.get(featureName);
            this.features.put(featureName, processor.process(feature));

        }
    }

    public static Instance create(ListedFeaturesMapper outputMapper) {
        Instance instance = new Instance();
        instance.to(outputMapper);
        return instance;
    }

    public static Instance create(NamedFeaturesMapper outputMapper) {
        Instance instance = new Instance();
        instance.to(outputMapper);
        return instance;
    }

    public static Instance get(TridentTuple tuple) {
        try {
            Instance instance = (Instance) tuple.getValueByField(INSTANCE_FIELD);
            return instance;
        } catch (Exception ex) {
            throw new IllegalStateException("No instance found in tuple " + tuple, ex);
        }
    }

    public static Instance get(TridentTuple tuple, ListedFeaturesMapper inputMapper) {
        Instance instance = Instance.get(tuple);
        return instance.from(inputMapper);
    }

    public static Instance get(TridentTuple tuple, NamedFeaturesMapper inputMapper) {
        Instance instance = Instance.get(tuple);
        return instance.from(inputMapper);
    }

    public static Instance get(TridentTuple tuple, ListedFeaturesMapper inputMapper, ListedFeaturesMapper outputMapper) {
        Instance instance = Instance.get(tuple);
        return instance.from(inputMapper).to(outputMapper);
    }

    public static Instance get(TridentTuple tuple, ListedFeaturesMapper inputMapper, NamedFeaturesMapper outputMapper) {
        Instance instance = Instance.get(tuple);
        return instance.from(inputMapper).to(outputMapper);
    }

    public static Instance get(TridentTuple tuple, NamedFeaturesMapper inputMapper, ListedFeaturesMapper outputMapper) {
        Instance instance = Instance.get(tuple);
        return instance.from(inputMapper).to(outputMapper);
    }

    public static Instance get(TridentTuple tuple, NamedFeaturesMapper inputMapper, NamedFeaturesMapper outputMapper) {
        Instance instance = Instance.get(tuple);
        return instance.from(inputMapper).to(outputMapper);
    }

    private Instance from(ListedFeaturesMapper inputListedFeaturesMapper) {
        this.inputListedFeaturesMapper = inputListedFeaturesMapper;
        this.inputNamedFeaturesMapper = null;
        return this;
    }

    private Instance from(NamedFeaturesMapper inputNamedFeaturesMapper) {
        this.inputListedFeaturesMapper = null;
        this.inputNamedFeaturesMapper = inputNamedFeaturesMapper;
        return this;
    }

    private Instance to(ListedFeaturesMapper outputListedFeaturesMapper) {
        this.outputListedFeaturesMapper = outputListedFeaturesMapper;
        this.outputNamedFeaturesMapper = null;
        return this;
    }

    private Instance to(NamedFeaturesMapper outputNamedFeaturesMapper) {
        this.outputListedFeaturesMapper = null;
        this.outputNamedFeaturesMapper = outputNamedFeaturesMapper;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((features == null) ? 0 : features.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Instance other = (Instance) obj;
        if (features == null) {
            if (other.features != null)
                return false;
        } else if (!features.equals(other.features))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Instance [label=" + label + ", features=" + features + "]";
    }

    public static interface FeatureProcessor<T> {
        T process(T feature);
    }
}
