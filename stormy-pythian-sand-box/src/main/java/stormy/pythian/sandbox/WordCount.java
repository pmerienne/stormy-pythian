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
import static stormy.pythian.model.annotation.MappingType.FIXED_FEATURES;
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
import stormy.pythian.model.annotation.Mapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.State;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.InputFixedFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFixedFeaturesMapper;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@Documentation(name = "Word count", type = ANALYTICS)
public class WordCount implements Component {

	private static final long serialVersionUID = 1822765078810762926L;

	public static final String WORD_FEATURE = "word";
	public static final String COUNT_FEATURE = "count";

	@InputStream(name = "in", type = FIXED_FEATURES, expectedFeatures = { @ExpectedFeature(name = WORD_FEATURE, type = String.class) })
	private Stream in;

	@OutputStream(name = "out", from = "in", newFeatures = { @ExpectedFeature(name = COUNT_FEATURE, type = Integer.class) })
	private Stream out;

	@Mapper(stream = "in")
	private InputFixedFeaturesMapper inputMapper;

	@Mapper(stream = "out")
	private OutputFixedFeaturesMapper outputMapper;
	
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
		private final InputFixedFeaturesMapper inputMapper;

		public ExtractFeature(String featureName, InputFixedFeaturesMapper mapper) {
			this.featureName = featureName;
			this.inputMapper = mapper;
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance instance = Instance.from(tuple);
			Object feature = instance.getInputFeature(inputMapper, featureName);
			collector.emit(new Values(feature));
		}

	}

	@SuppressWarnings("serial")
	private static class AddCountFeature extends BaseFunction {

		private final OutputFixedFeaturesMapper outMapper;

		public AddCountFeature(OutputFixedFeaturesMapper outMapper) {
			this.outMapper = outMapper;
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance original = Instance.from(tuple);

			Long count = tuple.getLongByField(COUNT_FEATURE);
			Instance updated = original.withFeature(outMapper, COUNT_FEATURE, count);

			collector.emit(new Values(updated));
		}
	}

}
