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

import static com.google.common.base.Objects.firstNonNull;
import static stormy.pythian.model.annotation.MappingType.FIXED_FEATURES;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import static stormy.pythian.model.instance.Instance.NEW_INSTANCE_FIELD;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.CombinerAggregator;
import storm.trident.operation.TridentCollector;
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
import stormy.pythian.model.instance.OutputFeaturesMapper;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@Documentation(name = "Feature mean", description = "Compute mean of each feature occurence")
public class DistinctFeatureMean implements Component {

	public static final String GROUP_BY_FEATURE = "Group by";
	public static final String MEAN_ON_FEATURE = "Mean on";
	public static final String MEAN_FEATURE = "Feature mean";

	private static final String GROUP_BY_FIELD = "GROUP_BY_FIELD";
	private static final String MEAN_FIELD = "MEAN_FIELD";

	@InputStream(name = "in", type = FIXED_FEATURES, expectedFeatures = { @ExpectedFeature(name = GROUP_BY_FEATURE, type = Object.class), @ExpectedFeature(name = MEAN_ON_FEATURE, type = Number.class) })
	private Stream in;

	@OutputStream(name = "out", from = "in", newFeatures = { @ExpectedFeature(name = MEAN_FEATURE, type = Double.class) })
	private Stream out;

	@Mapper(stream = "in")
	private InputFixedFeaturesMapper inputMapper;

	@Mapper(stream = "out")
	private OutputFeaturesMapper outputMapper;

	@State(name = "Count's state")
	private StateFactory stateFactory;

	private static final long serialVersionUID = -5312700259983804231L;

	@Override
	public void init() {
		TridentState counts = in //
				.each(new Fields(INSTANCE_FIELD), new ExtractGroupByFeature(inputMapper), new Fields(GROUP_BY_FIELD)) //
				.groupBy(new Fields(GROUP_BY_FIELD)) //
				.persistentAggregate(stateFactory, new Fields(INSTANCE_FIELD), new MeanAggregator(inputMapper), new Fields(MEAN_FIELD));

		out = in //
		.each(new Fields(INSTANCE_FIELD), new ExtractGroupByFeature(inputMapper), new Fields(GROUP_BY_FIELD)) //
				.stateQuery(counts, new Fields(GROUP_BY_FIELD), new MapGet(), new Fields(MEAN_FIELD)) //
				.each(new Fields(INSTANCE_FIELD, MEAN_FIELD), new AddMeanFeature(outputMapper), new Fields(NEW_INSTANCE_FIELD));
	}

	private static class ExtractGroupByFeature extends BaseFunction {

		private static final long serialVersionUID = -2823417821288444544L;

		private final InputFixedFeaturesMapper inputMapper;

		public ExtractGroupByFeature(InputFixedFeaturesMapper mapper) {
			this.inputMapper = mapper;
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance instance = Instance.from(tuple);

			Object groupByFeature = instance.getFeature(inputMapper, GROUP_BY_FEATURE);
			collector.emit(new Values(groupByFeature));
		}

	}

	private static class MeanAggregator implements CombinerAggregator<MeanState> {

		private static final long serialVersionUID = -5948600275983221714L;

		private final InputFixedFeaturesMapper inputMapper;

		public MeanAggregator(InputFixedFeaturesMapper inputMapper) {
			this.inputMapper = inputMapper;
		}

		@Override
		public MeanState init(TridentTuple tuple) {
			Instance instance = Instance.from(tuple);
			Number feature = instance.getFeature(inputMapper, MEAN_ON_FEATURE);

			return new MeanState(feature.doubleValue(), 1);
		}

		@Override
		public MeanState combine(MeanState val1, MeanState val2) {
			val1 = firstNonNull(val1, zero());
			val2 = firstNonNull(val2, zero());

			return new MeanState(val1.sum + val2.sum, val1.count + val2.count);
		}

		@Override
		public MeanState zero() {
			return new MeanState(0, 0);
		}

	}

	private static class AddMeanFeature extends BaseFunction {

		private static final long serialVersionUID = 2405388485852452769L;

		private final OutputFeaturesMapper outputMapper;

		public AddMeanFeature(OutputFeaturesMapper outputMapper) {
			this.outputMapper = outputMapper;
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance original = Instance.from(tuple);
			MeanState meanState = (MeanState) tuple.getValueByField(MEAN_FIELD);
			Double mean = meanState != null ? meanState.getMean() : null;

			Instance newInstance = original.withFeature(outputMapper, MEAN_FEATURE, mean);
			collector.emit(new Values(newInstance));
		}

	}

	public static class MeanState {
		private final double sum;
		private final long count;

		public MeanState(double sum, long count) {
			this.sum = sum;
			this.count = count;
		}

		public double getMean() {
			return sum / count;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (count ^ (count >>> 32));
			long temp;
			temp = Double.doubleToLongBits(sum);
			result = prime * result + (int) (temp ^ (temp >>> 32));
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
			MeanState other = (MeanState) obj;
			if (count != other.count)
				return false;
			if (Double.doubleToLongBits(sum) != Double.doubleToLongBits(other.sum))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "MeanState [sum=" + sum + ", count=" + count + "]";
		}

	}
}
