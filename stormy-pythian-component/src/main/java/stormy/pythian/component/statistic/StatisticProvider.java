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
package stormy.pythian.component.statistic;

import static stormy.pythian.model.annotation.ComponentType.ANALYTICS;
import static stormy.pythian.model.instance.FeatureType.DATE;
import static stormy.pythian.model.instance.FeatureType.DECIMAL;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import static stormy.pythian.model.instance.Instance.NEW_INSTANCE_FIELD;
import java.util.Date;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.CombinerAggregator;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.builtin.MapGet;
import storm.trident.operation.builtin.SnapshotGet;
import storm.trident.state.StateFactory;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.component.common.ExtractFeatures;
import stormy.pythian.component.statistic.aggregation.AggregableCount;
import stormy.pythian.component.statistic.aggregation.AggregableMean;
import stormy.pythian.component.statistic.aggregation.AggregableStatistic;
import stormy.pythian.component.statistic.aggregation.Operation;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.NameMapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.annotation.State;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.DecimalFeature;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.NamedFeaturesMapper;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@SuppressWarnings("serial")
@Documentation(name = "Statistic provider", type = ANALYTICS)
public class StatisticProvider implements Component {

    public static final String DATE_FEATURE = "Date";
    public static final String GROUP_BY_FEATURE = "Group by";
    public static final String PROCESSED_FEATURE = "Compute on";

    public static final String RESULT_FEATURE = "Result";

    public static final String GROUP_BY_FIELD = "GROUP_BY_FIELD";
    public static final String SLOT_FIELD = "SLOT_FIELD";
    public static final String COMPUTED_FEATURE_FIELD = "COMPUTED_FEATURE_FIELD";
    public static final String STATISTIC_FIELD = "STATISTIC_FIELD";
    public static final String SLOT_STATISTIC_FIELD = "SLOT_STATISTIC_FIELD";
    public static final String DATE_FIELD = "DATE_FIELD";

    @InputStream(name = "in")
    private Stream in;

    @OutputStream(name = "out", from = "in")
    private Stream out;

    @NameMapper(stream = "in", expectedFeatures = {
            @ExpectedFeature(name = GROUP_BY_FEATURE),
            @ExpectedFeature(name = DATE_FEATURE, type = DATE),
            @ExpectedFeature(name = PROCESSED_FEATURE, type = DECIMAL)
    })
    private NamedFeaturesMapper inputMapper;

    @NameMapper(stream = "out", expectedFeatures = { @ExpectedFeature(name = RESULT_FEATURE, type = DECIMAL) })
    private NamedFeaturesMapper outputMapper;

    @State(name = "Statistics' state")
    private StateFactory stateFactory;

    @Property(name = "Window length (in ms)", mandatory = false)
    private Long windowLengthMs;

    @Property(name = "Slot precision (in ms)", mandatory = false)
    private Long slotLengthMs;

    @Property(name = "Operation")
    private Operation statisticMethod;

    @Override
    public void init() {
        AggregableStatistic<?> aggregableStatistic = null;
        switch (statisticMethod) {
            case COUNT:
                aggregableStatistic = new AggregableCount();
                break;
            case MEAN:
                aggregableStatistic = new AggregableMean();
                break;
            default:
                throw new UnsupportedOperationException(statisticMethod + " unknwon");
        }

        boolean grouped = inputMapper.isSet(GROUP_BY_FEATURE);
        boolean timeWindowed = inputMapper.isSet(DATE_FEATURE) && windowLengthMs != null && slotLengthMs != null;

        if (grouped && timeWindowed) {
            out = initGroupedAndWindowedOutputStream(aggregableStatistic);
        } else if (grouped) {
            out = initGroupedOutputStream(aggregableStatistic);
        } else if (timeWindowed) {
            out = initWindowedOutputStream(aggregableStatistic);
        } else {
            out = initOutputStream(aggregableStatistic);
        }
    }

    private Stream initOutputStream(AggregableStatistic<?> aggregableStatistic) {
        TridentState statistics = in
                .shuffle()
                .persistentAggregate(stateFactory, new Fields(INSTANCE_FIELD), new StatisticAggregator<>(inputMapper, aggregableStatistic), new Fields(STATISTIC_FIELD));

        return in
                .stateQuery(statistics, new SnapshotGet(), new Fields(STATISTIC_FIELD))
                .each(new Fields(INSTANCE_FIELD, STATISTIC_FIELD), new AddStatisticFeatures<>(aggregableStatistic, inputMapper, outputMapper), new Fields(NEW_INSTANCE_FIELD));
    }

    private Stream initGroupedOutputStream(AggregableStatistic<?> aggregableStatistic) {
        TridentState statistics = in
                .each(new Fields(INSTANCE_FIELD), new ExtractFeatures(inputMapper, GROUP_BY_FEATURE), new Fields(GROUP_BY_FIELD))
                .groupBy(new Fields(GROUP_BY_FIELD))
                .persistentAggregate(stateFactory, new Fields(INSTANCE_FIELD), new StatisticAggregator<>(inputMapper, aggregableStatistic), new Fields(STATISTIC_FIELD));

        return in
                .each(new Fields(INSTANCE_FIELD), new ExtractFeatures(inputMapper, GROUP_BY_FEATURE), new Fields(GROUP_BY_FIELD))
                .stateQuery(statistics, new Fields(GROUP_BY_FIELD), new MapGet(), new Fields(STATISTIC_FIELD))
                .each(new Fields(INSTANCE_FIELD, STATISTIC_FIELD), new AddStatisticFeatures<>(aggregableStatistic, inputMapper, outputMapper), new Fields(NEW_INSTANCE_FIELD));
    }

    private Stream initWindowedOutputStream(AggregableStatistic<?> aggregableStatistic) {
        TridentState statistics = in
                .each(new Fields(INSTANCE_FIELD), new ExtractFeatures(inputMapper, DATE_FEATURE), new Fields(DATE_FIELD))
                .each(new Fields(DATE_FIELD), new DateToSlotIndex(slotLengthMs), new Fields(SLOT_FIELD))
                .groupBy(new Fields(SLOT_FIELD))
                .persistentAggregate(stateFactory, new Fields(INSTANCE_FIELD), new StatisticAggregator<>(inputMapper, aggregableStatistic), new Fields(STATISTIC_FIELD));

        return in
                .each(new Fields(INSTANCE_FIELD), new ExtractFeatures(inputMapper, DATE_FEATURE), new Fields(DATE_FIELD))
                .each(new Fields(DATE_FIELD), new EmitEventSlots(windowLengthMs, slotLengthMs), new Fields(SLOT_FIELD))
                .stateQuery(statistics, new Fields(SLOT_FIELD), new MapGet(), new Fields(SLOT_STATISTIC_FIELD))
                .groupBy(new Fields(INSTANCE_FIELD))
                .aggregate(new Fields(INSTANCE_FIELD, SLOT_STATISTIC_FIELD), new GlobalStatisticAggregator<>(aggregableStatistic), new Fields(STATISTIC_FIELD))
                .each(new Fields(INSTANCE_FIELD, STATISTIC_FIELD), new AddStatisticFeatures<>(aggregableStatistic, inputMapper, outputMapper), new Fields(NEW_INSTANCE_FIELD));
    }

    private Stream initGroupedAndWindowedOutputStream(AggregableStatistic<?> aggregableStatistic) {
        TridentState statistics = in
                .each(new Fields(INSTANCE_FIELD), new ExtractFeatures(inputMapper, PROCESSED_FEATURE, GROUP_BY_FEATURE, DATE_FEATURE),
                        new Fields(COMPUTED_FEATURE_FIELD, GROUP_BY_FIELD, DATE_FIELD))
                .each(new Fields(DATE_FIELD), new DateToSlotIndex(slotLengthMs), new Fields(SLOT_FIELD))
                .groupBy(new Fields(GROUP_BY_FIELD, SLOT_FIELD))
                .persistentAggregate(stateFactory, new Fields(INSTANCE_FIELD), new StatisticAggregator<>(inputMapper, aggregableStatistic), new Fields(STATISTIC_FIELD));

        return in
                .each(new Fields(INSTANCE_FIELD), new ExtractFeatures(inputMapper, DATE_FEATURE, GROUP_BY_FEATURE), new Fields(DATE_FIELD, GROUP_BY_FIELD))
                .each(new Fields(DATE_FIELD), new EmitEventSlots(windowLengthMs, slotLengthMs), new Fields(SLOT_FIELD))
                .stateQuery(statistics, new Fields(GROUP_BY_FIELD, SLOT_FIELD), new MapGet(), new Fields(SLOT_STATISTIC_FIELD))
                .groupBy(new Fields(INSTANCE_FIELD))
                .aggregate(new Fields(INSTANCE_FIELD, SLOT_STATISTIC_FIELD), new GlobalStatisticAggregator<>(aggregableStatistic), new Fields(STATISTIC_FIELD))
                .each(new Fields(INSTANCE_FIELD, STATISTIC_FIELD), new AddStatisticFeatures<>(aggregableStatistic, inputMapper, outputMapper), new Fields(NEW_INSTANCE_FIELD));
    }

    private static class DateToSlotIndex extends BaseFunction {

        private static final long serialVersionUID = -2823417821288444544L;

        private final long slotPrecisionMs;

        public DateToSlotIndex(long slotPrecisionMs) {
            this.slotPrecisionMs = slotPrecisionMs;
        }

        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            Date date = (Date) tuple.getValueByField(DATE_FIELD);
            long timestamp = date.getTime();
            long slotIndex = timestamp - timestamp % slotPrecisionMs;

            collector.emit(new Values(slotIndex));
        }
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
            Date date = (Date) tuple.getValueByField(DATE_FIELD);
            long timestamp = date.getTime();
            long baseSlotIndex = timestamp - timestamp % slotLengthMs;

            for (int i = 0; i < nbSlots; i++) {
                long slotIndex = baseSlotIndex - i * slotLengthMs;
                collector.emit(new Values(slotIndex));
            }
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

    private static class AddStatisticFeatures<T> extends BaseFunction {

        private static final long serialVersionUID = 2405388485852452769L;

        private final AggregableStatistic<T> aggregableStatistic;
        private final NamedFeaturesMapper inputMapper;
        private final NamedFeaturesMapper outputMapper;

        public AddStatisticFeatures(AggregableStatistic<T> aggregableStatistic, NamedFeaturesMapper inputMapper, NamedFeaturesMapper outputMapper) {
            this.aggregableStatistic = aggregableStatistic;
            this.inputMapper = inputMapper;
            this.outputMapper = outputMapper;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            T statistic = (T) tuple.getValueByField(STATISTIC_FIELD);
            Double feature = aggregableStatistic.toFeature(statistic);

            Instance instance = Instance.get(tuple, inputMapper, outputMapper);
            instance.setFeature(RESULT_FEATURE, new DecimalFeature(feature));

            collector.emit(new Values(instance));
        }
    }

    private static class StatisticAggregator<T> implements CombinerAggregator<T> {

        private static final long serialVersionUID = -5948600275983221714L;

        private final NamedFeaturesMapper inputMapper;
        private final AggregableStatistic<T> aggregableStatistic;

        public StatisticAggregator(NamedFeaturesMapper inputMapper, AggregableStatistic<T> aggregableStatistic) {
            this.inputMapper = inputMapper;
            this.aggregableStatistic = aggregableStatistic;
        }

        @Override
        public T init(TridentTuple tuple) {
            Instance instance = Instance.get(tuple, inputMapper);
            Double feature = instance.getFeature(PROCESSED_FEATURE).decimalValue();

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

    }

}
