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

import static stormy.pythian.component.analytics.Constants.COMPUTED_FEATURE;
import static stormy.pythian.component.analytics.Constants.COMPUTED_FEATURE_FIELD;
import static stormy.pythian.component.analytics.Constants.DATE_FEATURE;
import static stormy.pythian.component.analytics.Constants.DATE_FIELD;
import static stormy.pythian.component.analytics.Constants.GROUP_BY_FEATURE;
import static stormy.pythian.component.analytics.Constants.GROUP_BY_FIELD;
import static stormy.pythian.component.analytics.Constants.SLOT_FIELD;
import static stormy.pythian.component.analytics.Constants.SLOT_STATISTIC_FIELD;
import static stormy.pythian.component.analytics.Constants.STATISTIC_FIELD;
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
import stormy.pythian.component.analytics.StatisticAggregator.AddStatisticFeatures;
import stormy.pythian.component.analytics.StatisticAggregator.AggregableStatistic;
import stormy.pythian.component.common.ExtractFeatures;
import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.Mapper;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.annotation.State;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.InputFixedFeaturesMapper;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public abstract class AbstractTimeWindowFeatureStatistic<T> implements Component {

	@InputStream(name = "in", type = FIXED_FEATURES, expectedFeatures = { //
	@ExpectedFeature(name = GROUP_BY_FEATURE, type = Object.class), //
			@ExpectedFeature(name = DATE_FEATURE, type = Long.class), //
			@ExpectedFeature(name = COMPUTED_FEATURE, type = Number.class) //
	})
	private Stream in;

	@Mapper(stream = "in")
	private InputFixedFeaturesMapper inputMapper;

	@State(name = "Statistics' state")
	private StateFactory stateFactory;

	@Property(name = "Window length (in ms)")
	private long windowLengthMs;

	@Property(name = "Slot precision (in ms)")
	private long slotLengthMs;

	private static final long serialVersionUID = -5312700259983804231L;

	public Stream initOutputStream(AggregableStatistic<T> aggregableStatistic) {
		TridentState statistics = in //
				.each(new Fields(INSTANCE_FIELD), new ExtractFeatures(inputMapper, COMPUTED_FEATURE, GROUP_BY_FEATURE, DATE_FEATURE), new Fields(COMPUTED_FEATURE_FIELD, GROUP_BY_FIELD, DATE_FIELD)) //
				.each(new Fields(DATE_FIELD), new DateToSlotIndex(slotLengthMs), new Fields(SLOT_FIELD)) //
				.groupBy(new Fields(GROUP_BY_FIELD, SLOT_FIELD)) //
				.persistentAggregate(stateFactory, new Fields(INSTANCE_FIELD), new StatisticAggregator<>(inputMapper, aggregableStatistic), new Fields(STATISTIC_FIELD)); //

		return in //
				.each(new Fields(INSTANCE_FIELD), new ExtractFeatures(inputMapper, DATE_FEATURE, GROUP_BY_FEATURE), new Fields(DATE_FIELD, GROUP_BY_FIELD)) //
				.each(new Fields(DATE_FIELD), new EmitEventSlots(windowLengthMs, slotLengthMs), new Fields(SLOT_FIELD)) //
				.stateQuery(statistics, new Fields(GROUP_BY_FIELD, SLOT_FIELD), new MapGet(), new Fields(SLOT_STATISTIC_FIELD)) //
				.groupBy(new Fields(INSTANCE_FIELD)) //
				.aggregate(new Fields(INSTANCE_FIELD, SLOT_STATISTIC_FIELD), new GlobalStatisticAggregator<>(aggregableStatistic), new Fields(STATISTIC_FIELD)) //
				.each(new Fields(INSTANCE_FIELD, STATISTIC_FIELD), new AddStatisticFeatures<T>(aggregableStatistic), new Fields(NEW_INSTANCE_FIELD));
	}

	private static class EmitEventSlots extends BaseFunction {

		private static final long serialVersionUID = -6347463039949939944L;

		private final long slotLengthMs;
		private final int nbSlots;

		public EmitEventSlots(long windowLengthMs, long slotLengthMs) {
			this.slotLengthMs = slotLengthMs;
			this.nbSlots = Long.valueOf((windowLengthMs / slotLengthMs)).intValue();
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			long date = (long) tuple.getValueByField(DATE_FIELD);
			long baseSlotIndex = date - date % slotLengthMs;

			for (int i = 0; i < nbSlots; i++) {
				long slotIndex = baseSlotIndex - i * slotLengthMs;
				collector.emit(new Values(slotIndex));
			}
		}

	}

	private static class DateToSlotIndex extends BaseFunction {

		private static final long serialVersionUID = -2823417821288444544L;

		private final long slotPrecisionMs;

		public DateToSlotIndex(long slotPrecisionMs) {
			this.slotPrecisionMs = slotPrecisionMs;
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			long date = (long) tuple.getValueByField(DATE_FIELD);
			long slotIndex = date - date % slotPrecisionMs;

			collector.emit(new Values(slotIndex));
		}

	}

	private static class GlobalStatisticAggregator<T> implements CombinerAggregator<T> {

		private static final long serialVersionUID = -6358197247831377615L;

		private final AggregableStatistic<T> aggregableStatistic;

		public GlobalStatisticAggregator(AggregableStatistic<T> aggregableStatistic) {
			this.aggregableStatistic = aggregableStatistic;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T init(TridentTuple tuple) {
			try {
				T stat = (T) tuple.getValueByField(SLOT_STATISTIC_FIELD);
				return stat == null ? zero() : stat;
			} catch (NullPointerException ex) {
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

	}
}
