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
package stormy.pythian.core.sandbox;

import static stormy.pythian.model.annotation.ComponentType.ANALYTICS;
import static stormy.pythian.model.annotation.MappingType.FIXED_FEATURES;
import static stormy.pythian.model.instance.FeatureType.INTEGER;
import static stormy.pythian.model.instance.FeatureType.TEXT;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.builtin.Count;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.testing.MemoryMapState;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.FeaturesMapper;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.Feature;
import stormy.pythian.model.instance.FixedFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.LongFeature;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@Documentation(name = "Word count", type = ANALYTICS)
public class WordCount implements Component {

	private static final long serialVersionUID = 1822765078810762926L;

	public static final String WORD_FEATURE = "word";
	public static final String COUNT_FEATURE = "count";

	@InputStream(name = "in", type = FIXED_FEATURES, expectedFeatures = { @ExpectedFeature(name = WORD_FEATURE, type = TEXT) })
	private Stream in;

	@OutputStream(name = "out", from = "in", newFeatures = { @ExpectedFeature(name = COUNT_FEATURE, type = INTEGER) })
	private Stream out;

	@FeaturesMapper(stream = "in")
	private FixedFeaturesMapper inputMapper;

	@FeaturesMapper(stream = "out")
	private FixedFeaturesMapper outputMapper;

	@Override
	public void init() {
		TridentState wordCounts = in//
				.each(new Fields(INSTANCE_FIELD), new WordExtractor(inputMapper), new Fields("word")) //
				.groupBy(new Fields("word")) //
				.persistentAggregate(new MemoryMapState.Factory(), new Fields("word"), new Count(), new Fields("count")); //

		out = in //
		.stateQuery(wordCounts, new Fields(INSTANCE_FIELD), new GetCountFeature(inputMapper, outputMapper), new Fields());

	}

	@SuppressWarnings("serial")
	private static class WordExtractor extends BaseFunction {

		private final FixedFeaturesMapper inputMapper;

		public WordExtractor(FixedFeaturesMapper inputMapper) {
			this.inputMapper = inputMapper;
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance instance = Instance.from(tuple);
			Feature<String> word = inputMapper.getFeature(instance, WORD_FEATURE);
			collector.emit(new Values(word.getValue()));
		}
	}

	@SuppressWarnings({ "serial", "rawtypes" })
	private static class GetCountFeature extends BaseQueryFunction<MapState<Long>, Long> {

		private final FixedFeaturesMapper inputMapper;
		private final FixedFeaturesMapper outputMapper;

		public GetCountFeature(FixedFeaturesMapper inputMapper, FixedFeaturesMapper outputMapper) {
			this.inputMapper = inputMapper;
			this.outputMapper = outputMapper;
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<Long> batchRetrieve(MapState<Long> map, List<TridentTuple> tuples) {
			List<List<String>> keys = new ArrayList<>(tuples.size());

			for (TridentTuple tuple : tuples) {
				Instance instance = Instance.from(tuple);
				Feature<String> wordFeature = inputMapper.getFeature(instance, WORD_FEATURE);
				keys.add(Arrays.asList(wordFeature.getValue()));
			}

			return map.multiGet((List) keys);
		}

		@Override
		public void execute(TridentTuple tuple, Long count, TridentCollector collector) {
			Instance instance = Instance.from(tuple);
			outputMapper.setFeature(instance, COUNT_FEATURE, new LongFeature(count == null ? 0 : count));

			collector.emit(new Values(instance));
		}
	}

}
