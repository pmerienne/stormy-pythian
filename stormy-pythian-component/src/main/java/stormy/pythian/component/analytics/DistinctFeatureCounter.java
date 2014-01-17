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
package stormy.pythian.component.analytics;

import static stormy.pythian.model.annotation.MappingType.USER_SELECTION;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import static stormy.pythian.model.instance.Instance.NEW_INSTANCE_FIELD;
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
import stormy.pythian.model.instance.InputUserSelectionFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFeaturesMapper;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@Documentation(name = "Count", description = "Count features occurence.")
public class DistinctFeatureCounter implements Component {

	public static final String SELECTED_FEATURES = "Feature";
	public static final String COUNT_FEATURE = "Feature count";

	private static final String SELECTED_FEATURES_FIELD = "SELECTED_FEATURES_FIELD";
	private static final String COUNT_FIELD = "COUNT_FIELD";

	@InputStream(name = "in", type = USER_SELECTION)
	private Stream in;

	@OutputStream(name = "out", from = "in", newFeatures = { @ExpectedFeature(name = COUNT_FEATURE, type = Long.class) })
	private Stream out;

	@Mapper(stream = "in")
	private InputUserSelectionFeaturesMapper inputMapper;

	@Mapper(stream = "out")
	private OutputFeaturesMapper outputMapper;
	
	@State(name = "Count's state")
	private StateFactory stateFactory;

	private static final long serialVersionUID = -5312700259983804231L;

	@Override
	public void init() {
		TridentState counts = in //
				.each(new Fields(INSTANCE_FIELD), new ExtractFeatures(inputMapper), new Fields(SELECTED_FEATURES_FIELD)) //
				.groupBy(new Fields(SELECTED_FEATURES_FIELD)) //
				.persistentAggregate(stateFactory, new Fields(SELECTED_FEATURES_FIELD), new Count(), new Fields(COUNT_FIELD)); //

		out = in //
		.each(new Fields(INSTANCE_FIELD), new ExtractFeatures(inputMapper), new Fields(SELECTED_FEATURES_FIELD)) //
				.stateQuery(counts, new Fields(SELECTED_FEATURES_FIELD), new MapGet(), new Fields(COUNT_FIELD)) //
				.each(new Fields(INSTANCE_FIELD, COUNT_FIELD), new AddFeatures(outputMapper), new Fields(NEW_INSTANCE_FIELD));

	}

	private static class ExtractFeatures extends BaseFunction {

		private static final long serialVersionUID = -2823417821288444544L;

		private final InputUserSelectionFeaturesMapper inputMapper;

		public ExtractFeatures(InputUserSelectionFeaturesMapper mapper) {
			this.inputMapper = mapper;
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance instance = Instance.from(tuple);
			Object[] selectedFeatures = instance.getSelectedFeatures(inputMapper);
			collector.emit(new Values(selectedFeatures));
		}

	}

	private static class AddFeatures extends BaseFunction {

		private static final long serialVersionUID = 2405388485852452769L;

		private final OutputFeaturesMapper outputMapper;

		public AddFeatures(OutputFeaturesMapper outputMapper) {
			this.outputMapper = outputMapper;
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance original = Instance.from(tuple);
			Long count = tuple.getLongByField(COUNT_FIELD);

			Instance newInstance = original.withFeature(outputMapper, COUNT_FEATURE, count);
			collector.emit(new Values(newInstance));
		}

	}

}
