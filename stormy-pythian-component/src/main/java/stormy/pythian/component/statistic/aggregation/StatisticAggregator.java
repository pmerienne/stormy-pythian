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
package stormy.pythian.component.statistic.aggregation;

import static stormy.pythian.component.statistic.aggregation.Constants.COMPUTED_FEATURE;
import static stormy.pythian.component.statistic.aggregation.Constants.STATISTIC_FIELD;

import java.io.Serializable;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.CombinerAggregator;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.instance.InputFixedFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import backtype.storm.tuple.Values;

public class StatisticAggregator<T> implements CombinerAggregator<T> {

	private static final long serialVersionUID = -5948600275983221714L;

	private final InputFixedFeaturesMapper inputMapper;
	private final AggregableStatistic<T> aggregableStatistic;

	public StatisticAggregator(InputFixedFeaturesMapper inputMapper, AggregableStatistic<T> aggregableStatistic) {
		this.inputMapper = inputMapper;
		this.aggregableStatistic = aggregableStatistic;
	}

	@Override
	public T init(TridentTuple tuple) {
		Instance instance = Instance.from(tuple);
		Number feature = instance.getFeature(inputMapper, COMPUTED_FEATURE);

		if (feature != null) {
			T statistic = aggregableStatistic.init(feature);
			return statistic;
		} else {
			return zero();
		}
	}

	@Override
	public T combine(T val1, T val2) {
		val1 = val1 != null ? val1 : zero();
		val2 = val2 != null ? val2 : zero();

		return aggregableStatistic.combine(val1, val2);
	}

	@Override
	public T zero() {
		return aggregableStatistic.zero();
	}

	public interface AggregableStatistic<T> extends Serializable {

		public T init(Number feature);

		public T combine(T val1, T val2);

		public T zero();

		Instance update(Instance original, T statistic);
	}

	public static class AddStatisticFeatures<T> extends BaseFunction {

		private static final long serialVersionUID = 2405388485852452769L;

		private final AggregableStatistic<T> aggregableStatistic;

		public AddStatisticFeatures(AggregableStatistic<T> aggregableStatistic) {
			this.aggregableStatistic = aggregableStatistic;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance original = Instance.from(tuple);
			T statistic = (T) tuple.getValueByField(STATISTIC_FIELD);
			Instance newInstance = aggregableStatistic.update(original, statistic);
			collector.emit(new Values(newInstance));
		}

	}

}