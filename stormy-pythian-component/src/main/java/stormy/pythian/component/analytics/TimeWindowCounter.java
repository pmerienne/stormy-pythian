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
import storm.trident.operation.builtin.Count;
import storm.trident.operation.builtin.MapGet;
import storm.trident.state.StateFactory;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.Mapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.annotation.State;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.InputFixedFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFeaturesMapper;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@Documentation(name = "Time window counter", description = "Count features occurence during a fixed time window.")
public class TimeWindowCounter implements Component {

	public static final String DATE_FEATURE = "Date";
	public static final String SELECTED_FEATURE = "Group by";
	public static final String COUNT_FEATURE = "Count";

	public static final String SLOT_FIELD = "SLOT_FIELD";
	public static final String SELECTED_FEATURE_FIELD = "SELECTED_FEATURE_FIELD";
	public static final String COUNT_FIELD = "COUNT_FIELD";
	public static final String GLOABL_COUNT_FIELD = "GLOABL_COUNT_FIELD";

	@InputStream(name = "in", type = FIXED_FEATURES, expectedFeatures = { @ExpectedFeature(name = DATE_FEATURE, type = Long.class), @ExpectedFeature(name = SELECTED_FEATURE, type = Object.class) })
	private transient Stream in;

	@OutputStream(name = "out", from = "in", newFeatures = { @ExpectedFeature(name = COUNT_FEATURE, type = Long.class) })
	private transient Stream out;

	@Mapper(stream = "in")
	private InputFixedFeaturesMapper inputMapper;

	@Mapper(stream = "out")
	private OutputFeaturesMapper outputMapper;

	@State(name = "Count's state")
	private StateFactory stateFactory;

	@Property(name = "Window length (in ms)")
	private long windowLengthMs;

	@Property(name = "Slot precision (in ms)")
	private long slotLengthMs;

	private static final long serialVersionUID = -5312700259983804231L;

	@Override
	public void init() {
		TridentState counts = in //
				.each(new Fields(INSTANCE_FIELD), new ExtractSelectedFeatureAndSlotIndex(inputMapper, slotLengthMs), new Fields(SELECTED_FEATURE_FIELD, SLOT_FIELD)) //
				.groupBy(new Fields(SELECTED_FEATURE_FIELD, SLOT_FIELD)) //
				.persistentAggregate(stateFactory, new Fields(SELECTED_FEATURE_FIELD, SLOT_FIELD), new Count(), new Fields(COUNT_FIELD)); //

		out = in //
		.each(new Fields(INSTANCE_FIELD), new ExtractSelectedFeature(inputMapper), new Fields(SELECTED_FEATURE_FIELD)) //
				.each(new Fields(INSTANCE_FIELD), new EmitEventSlots(inputMapper, windowLengthMs, slotLengthMs), new Fields(SLOT_FIELD)) //
				.stateQuery(counts, new Fields(SELECTED_FEATURE_FIELD, SLOT_FIELD), new MapGet(), new Fields(COUNT_FIELD)) //
				.groupBy(new Fields(INSTANCE_FIELD)) //
				.aggregate(new Fields(INSTANCE_FIELD, COUNT_FIELD), new SumCount(), new Fields(GLOABL_COUNT_FIELD)) //
				.each(new Fields(INSTANCE_FIELD, GLOABL_COUNT_FIELD), new AddCountFeature(outputMapper), new Fields(NEW_INSTANCE_FIELD));

	}

	private static class EmitEventSlots extends BaseFunction {

		private static final long serialVersionUID = -6347463039949939944L;

		private final InputFixedFeaturesMapper inputMapper;
		private final long slotLengthMs;
		private final int nbSlots;

		public EmitEventSlots(InputFixedFeaturesMapper inputMapper, long windowLengthMs, long slotLengthMs) {
			this.inputMapper = inputMapper;
			this.slotLengthMs = slotLengthMs;
			this.nbSlots = Long.valueOf((windowLengthMs / slotLengthMs)).intValue();
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance instance = Instance.from(tuple);
			long date = instance.getFeature(inputMapper, DATE_FEATURE);
			long baseSlotIndex = date - date % slotLengthMs;

			for (int i = 0; i < nbSlots; i++) {
				long slotIndex = baseSlotIndex - i * slotLengthMs;
				collector.emit(new Values(slotIndex));
			}
		}

	}

	private static class ExtractSelectedFeatureAndSlotIndex extends BaseFunction {

		private static final long serialVersionUID = -2823417821288444544L;

		private final InputFixedFeaturesMapper inputMapper;
		private final long slotPrecisionMs;

		public ExtractSelectedFeatureAndSlotIndex(InputFixedFeaturesMapper mapper, long slotPrecisionMs) {
			this.inputMapper = mapper;
			this.slotPrecisionMs = slotPrecisionMs;
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance instance = Instance.from(tuple);

			Object selectedFeature = instance.getFeature(inputMapper, SELECTED_FEATURE);

			long date = instance.getFeature(inputMapper, DATE_FEATURE);
			long slotIndex = date - date % slotPrecisionMs;

			collector.emit(new Values(selectedFeature, slotIndex));
		}

	}

	private static class ExtractSelectedFeature extends BaseFunction {

		private static final long serialVersionUID = -2823417821288444544L;

		private final InputFixedFeaturesMapper inputMapper;

		public ExtractSelectedFeature(InputFixedFeaturesMapper mapper) {
			this.inputMapper = mapper;
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance instance = Instance.from(tuple);
			Object selectedFeature = instance.getFeature(inputMapper, SELECTED_FEATURE);
			collector.emit(new Values(selectedFeature));
		}

	}

	private static class AddCountFeature extends BaseFunction {

		private static final long serialVersionUID = 2405388485852452769L;

		private final OutputFeaturesMapper outputMapper;

		public AddCountFeature(OutputFeaturesMapper outputMapper) {
			this.outputMapper = outputMapper;
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance original = Instance.from(tuple);
			Long count = tuple.getLongByField(GLOABL_COUNT_FIELD);

			Instance newInstance = original.withFeature(outputMapper, COUNT_FEATURE, count);
			collector.emit(new Values(newInstance));
		}

	}

	private class SumCount implements CombinerAggregator<Long> {

		private static final long serialVersionUID = -7181306074556717123L;

		@Override
		public Long init(TridentTuple tuple) {
			try {
				return tuple.getLongByField(COUNT_FIELD);
			} catch (Exception ex) {
				return 0L;
			}
		}

		@Override
		public Long combine(Long val1, Long val2) {
			val1 = firstNonNull(val1, 0L);
			val2 = firstNonNull(val2, 0L);

			return val1 + val2;
		}

		@Override
		public Long zero() {
			return 0L;
		}

	}

}
