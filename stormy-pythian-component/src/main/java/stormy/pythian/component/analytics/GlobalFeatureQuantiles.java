package stormy.pythian.component.analytics;

import static java.util.Arrays.asList;
import static stormy.pythian.model.annotation.MappingType.FIXED_FEATURES;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import static stormy.pythian.model.instance.Instance.NEW_INSTANCE_FIELD;

import java.util.HashMap;
import java.util.Map;

import org.apache.mahout.common.RandomUtils;

import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.CombinerAggregator;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.builtin.SnapshotGet;
import storm.trident.state.StateFactory;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.component.analytics.tdigest.TDigest;
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

@Documentation(name = "Global feature quantiles", description = "Computes quantiles of a feature")
public class GlobalFeatureQuantiles implements Component {

	public static final String SELECTED_FEATURE = "Feature";
	public static final String MEDIAN_FEATURE = "Feature median";
	public static final String QUANTILE_75th_FEATURE = "Feature 75th percentiles";
	public static final String QUANTILE_90th_FEATURE = "Feature 90th percentiles";
	public static final String QUANTILE_95th_FEATURE = "Feature 95th percentiles";
	public static final String QUANTILE_99th_FEATURE = "Feature 99th percentiles";
	public static final String QUANTILE_99th9_FEATURE = "Feature 99.9th percentiles";

	private static final String QUANTILES_STATE_FIELD = "QUANTILES_STATE_FIELD";

	@InputStream(name = "in", type = FIXED_FEATURES, expectedFeatures = { @ExpectedFeature(name = SELECTED_FEATURE, type = Number.class) })
	private Stream in;

	@OutputStream(name = "out", from = "in", newFeatures = { //
	@ExpectedFeature(name = MEDIAN_FEATURE, type = Double.class), //
			@ExpectedFeature(name = QUANTILE_75th_FEATURE, type = Double.class), //
			@ExpectedFeature(name = QUANTILE_90th_FEATURE, type = Double.class), //
			@ExpectedFeature(name = QUANTILE_95th_FEATURE, type = Double.class), //
			@ExpectedFeature(name = QUANTILE_99th_FEATURE, type = Double.class), //
			@ExpectedFeature(name = QUANTILE_99th9_FEATURE, type = Double.class) //
	})
	private Stream out;

	@Mapper(stream = "in")
	private InputFixedFeaturesMapper inputMapper;

	@Mapper(stream = "out")
	private OutputFeaturesMapper outputMapper;

	@State(name = "Quantiles' state")
	private StateFactory stateFactory;

	@Property(name = "Compression", description = "How should accuracy be traded for size? A value of N here will give quantile errors almost always less than 3/N with considerably smaller errors expected for extreme quantiles. Conversely, you should expect to track about 5 N centroids for this accuracy.")
	private double compression = 100.0;

	private static final long serialVersionUID = -5312700259983804231L;

	@Override
	public void init() {
		TridentState counts = in.shuffle() //
				.persistentAggregate(stateFactory, new Fields(INSTANCE_FIELD), new QuantilesAggregator(inputMapper, compression), new Fields(QUANTILES_STATE_FIELD));

		out = in.stateQuery(counts, new SnapshotGet(), new Fields(QUANTILES_STATE_FIELD)) //
				.each(new Fields(INSTANCE_FIELD, QUANTILES_STATE_FIELD), new AddQuantilesFeatures(outputMapper), new Fields(NEW_INSTANCE_FIELD));
	}

	private static class QuantilesAggregator implements CombinerAggregator<TDigest> {

		private static final long serialVersionUID = -5948600275983221714L;

		private final InputFixedFeaturesMapper inputMapper;
		private final double compression;

		public QuantilesAggregator(InputFixedFeaturesMapper inputMapper, double compression) {
			this.inputMapper = inputMapper;
			this.compression = compression;
		}

		@Override
		public TDigest init(TridentTuple tuple) {
			Instance instance = Instance.from(tuple);
			Number feature = instance.getFeature(inputMapper, SELECTED_FEATURE);

			TDigest quantilesState = new TDigest(compression);
			quantilesState.add(feature.doubleValue());
			return quantilesState;
		}

		@Override
		public TDigest combine(TDigest val1, TDigest val2) {
			if (val1 == null && val2 == null) {
				return null;
			} else if (val1 == null) {
				return val2;
			} else if (val2 == null) {
				return val1;
			}

			TDigest merged = TDigest.merge(compression, asList(val1, val2), RandomUtils.getRandom());
			return merged;
		}

		@Override
		public TDigest zero() {
			return null;
		}

	}

	private static class AddQuantilesFeatures extends BaseFunction {

		private static final long serialVersionUID = 2405388485852452769L;

		private final OutputFeaturesMapper outputMapper;

		public AddQuantilesFeatures(OutputFeaturesMapper outputMapper) {
			this.outputMapper = outputMapper;
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance original = Instance.from(tuple);
			TDigest tDigest = (TDigest) tuple.getValueByField(QUANTILES_STATE_FIELD);

			Map<String, Object> newFeatures = new HashMap<>(6);
			newFeatures.put(MEDIAN_FEATURE, getQuantile(tDigest, 0.5));
			newFeatures.put(QUANTILE_75th_FEATURE, getQuantile(tDigest, 0.75));
			newFeatures.put(QUANTILE_90th_FEATURE, getQuantile(tDigest, 0.9));
			newFeatures.put(QUANTILE_95th_FEATURE, getQuantile(tDigest, 0.95));
			newFeatures.put(QUANTILE_99th_FEATURE, getQuantile(tDigest, 0.99));
			newFeatures.put(QUANTILE_99th9_FEATURE, getQuantile(tDigest, 0.999));

			Instance newInstance = original.withFeatures(outputMapper, newFeatures);
			collector.emit(new Values(newInstance));
		}

		private Double getQuantile(TDigest digest, double q) {
			if (digest == null || digest.centroidCount() <= 1) {
				return null;
			} else {
				return digest.quantile(q);
			}

		}
	}

}
