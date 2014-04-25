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
package stormy.pythian.sandbox;

import static stormy.pythian.model.annotation.ComponentType.ANALYTICS;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.builtin.Count;
import storm.trident.operation.builtin.MapGet;
import storm.trident.state.StateFactory;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.NameMapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.State;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.NamedFeaturesMapper;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@Documentation(name = "Word count", type = ANALYTICS)
public class WordCount implements Component {

    private static final long serialVersionUID = 1822765078810762926L;

    public static final String WORD_FEATURE = "word";
    public static final String COUNT_FEATURE = "count";

    @InputStream(name = "in")
    private Stream in;

    @OutputStream(name = "out", from = "in")
    private Stream out;

    @NameMapper(stream = "in", expectedFeatures = { @ExpectedFeature(name = WORD_FEATURE, type = String.class) })
    private NamedFeaturesMapper inputMapper;

    @NameMapper(stream = "out", expectedFeatures = { @ExpectedFeature(name = COUNT_FEATURE, type = Integer.class) })
    private NamedFeaturesMapper outputMapper;

    @State(name = "count state")
    private StateFactory stateFactory;

    @Override
    public void init() {
        TridentState wordCounts = in//
                .each(new Fields(INSTANCE_FIELD), new ExtractFeature(WORD_FEATURE, inputMapper), new Fields(WORD_FEATURE)) //
                .groupBy(new Fields(WORD_FEATURE)) //
                .persistentAggregate(stateFactory, new Fields(WORD_FEATURE), new Count(), new Fields(COUNT_FEATURE)); //

        out = in //
        .each(new Fields(INSTANCE_FIELD), new ExtractFeature(WORD_FEATURE, inputMapper), new Fields(WORD_FEATURE)) //
                .stateQuery(wordCounts, new Fields(WORD_FEATURE), new MapGet(), new Fields(COUNT_FEATURE)) //
                .each(new Fields(INSTANCE_FIELD, COUNT_FEATURE), new AddCountFeature(outputMapper), new Fields(Instance.NEW_INSTANCE_FIELD));

    }

    @SuppressWarnings("serial")
    private static class ExtractFeature extends BaseFunction {

        private final String featureName;
        private final NamedFeaturesMapper inputMapper;

        public ExtractFeature(String featureName, NamedFeaturesMapper mapper) {
            this.featureName = featureName;
            this.inputMapper = mapper;
        }

        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            Instance instance = Instance.get(tuple, inputMapper);
            Object feature = instance.getFeature(featureName);
            collector.emit(new Values(feature));
        }

    }

    @SuppressWarnings("serial")
    private static class AddCountFeature extends BaseFunction {

        private final NamedFeaturesMapper outMappings;

        public AddCountFeature(NamedFeaturesMapper outMapper) {
            this.outMappings = outMapper;
        }

        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            Long count = tuple.getLongByField(COUNT_FEATURE);
            Instance instance = Instance.get(tuple, (NamedFeaturesMapper) null, outMappings);
            instance.setFeature(COUNT_FEATURE, count);
            collector.emit(new Values(instance));
        }
    }

}
