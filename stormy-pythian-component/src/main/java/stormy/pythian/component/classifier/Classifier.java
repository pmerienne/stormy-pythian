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
package stormy.pythian.component.classifier;

import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import static stormy.pythian.model.instance.Instance.NEW_INSTANCE_FIELD;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.StateFactory;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.ListMapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.annotation.State;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.ListedFeaturesMapper;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.github.pmerienne.trident.ml.util.KeysUtil;

@SuppressWarnings("serial")
public abstract class Classifier<L> implements Component {

    @InputStream(name = "update")
    private transient Stream update;

    @ListMapper(stream = "update")
    protected ListedFeaturesMapper updateInputMapper;

    @InputStream(name = "query")
    private transient Stream query;

    @ListMapper(stream = "query")
    protected ListedFeaturesMapper queryInputMapper;

    @OutputStream(name = "prediction", from = "query")
    private transient Stream prediction;

    @State(name = "Classifier's model")
    private transient StateFactory stateFactory;

    @Property(name = "Classifier name", mandatory = true)
    private String classifierName;

    protected abstract void update(L label, List<Object> features);

    protected abstract L classify(List<Object> features);

    protected abstract void initClassifier();

    @Override
    public void init() {
        initClassifier();
        TridentState classifierState = update.partitionPersist(stateFactory, new Fields(INSTANCE_FIELD), new ClassifierUpdater<L>(classifierName, this, updateInputMapper));
        prediction = query.stateQuery(classifierState, new Fields(INSTANCE_FIELD), new ClassifyQuery<L>(classifierName, queryInputMapper), new Fields(NEW_INSTANCE_FIELD));
    }

    private static class ClassifierUpdater<L> extends BaseStateUpdater<MapState<Classifier<L>>> {

        private final String classifierName;
        private final Classifier<L> initialClassifier;
        private final ListedFeaturesMapper mapper;

        public ClassifierUpdater(String classifierName, Classifier<L> initialClassifier, ListedFeaturesMapper mapper) {
            this.classifierName = classifierName;
            this.initialClassifier = initialClassifier;
            this.mapper = mapper;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void updateState(MapState<Classifier<L>> state, List<TridentTuple> tuples, TridentCollector collector) {
            // Get model
            List<Classifier<L>> classifiers = state.multiGet(KeysUtil.toKeys(this.classifierName));
            Classifier<L> classifier = null;
            if (classifiers != null && !classifiers.isEmpty()) {
                classifier = classifiers.get(0);
            }

            // Init it if necessary
            if (classifier == null) {
                classifier = this.initialClassifier;
            }

            // Update model
            for (TridentTuple tuple : tuples) {
                Instance instance = Instance.get(tuple, mapper);
                List<Object> features = instance.getFeatures();
                L label = (L) instance.getLabel();
                classifier.update(label, features);
            }

            // Save model
            state.multiPut(KeysUtil.toKeys(this.classifierName), Arrays.asList(classifier));
        }
    }

    private static class ClassifyQuery<L> extends BaseQueryFunction<MapState<Classifier<L>>, Instance> {

        private final String classifierName;
        private final ListedFeaturesMapper mapper;

        public ClassifyQuery(String classifierName, ListedFeaturesMapper mapper) {
            this.classifierName = classifierName;
            this.mapper = mapper;
        }

        @Override
        public List<Instance> batchRetrieve(MapState<Classifier<L>> state, List<TridentTuple> tuples) {
            List<Instance> instances = new ArrayList<Instance>();

            List<Classifier<L>> classifiers = state.multiGet(KeysUtil.toKeys(this.classifierName));
            if (classifiers != null && !classifiers.isEmpty()) {
                Classifier<L> classifier = classifiers.get(0);
                if (classifier == null) {
                    for (int i = 0; i < tuples.size(); i++) {
                        instances.add(null);
                    }
                } else {

                    for (TridentTuple tuple : tuples) {
                        Instance instance = Instance.get(tuple, mapper);
                        List<Object> features = instance.getFeatures();

                        L label = classifier.classify(features);
                        instance.setLabel(label);
                        instances.add(instance);
                    }
                }
            } else {
                for (int i = 0; i < tuples.size(); i++) {
                    instances.add(null);
                }
            }

            return instances;
        }

        public void execute(TridentTuple tuple, Instance instance, TridentCollector collector) {
            collector.emit(new Values(instance));
        }
    }
}
