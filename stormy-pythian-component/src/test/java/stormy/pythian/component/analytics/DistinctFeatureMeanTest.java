package stormy.pythian.component.analytics;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Delta.delta;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static stormy.pythian.component.analytics.DistinctFeatureMean.GROUP_BY_FEATURE;
import static stormy.pythian.component.analytics.DistinctFeatureMean.MEAN_FEATURE;
import static stormy.pythian.component.analytics.DistinctFeatureMean.MEAN_ON_FEATURE;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import static stormy.pythian.model.instance.InstanceTestBuilder.instance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import storm.trident.Stream;
import storm.trident.testing.FixedBatchSpout;
import storm.trident.testing.MemoryMapState;
import stormy.pythian.model.instance.FeaturesIndex;
import stormy.pythian.model.instance.InputFixedFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFeaturesMapper;
import stormy.pythian.testing.InstanceCollector;
import stormy.pythian.testing.TridentIntegrationTest;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class DistinctFeatureMeanTest extends TridentIntegrationTest {

	private static final int TOPOLOGY_START_TIME = 5000;

	@SuppressWarnings("unchecked")
	@Test
	public void should_mean_each_features_occurence() {
		// Given
		List<String> inputFeatures = Arrays.asList("username", "call duration");
		Map<String, String> inputMappings = new HashMap<>();
		inputMappings.put(MEAN_ON_FEATURE, "call duration");
		inputMappings.put(GROUP_BY_FEATURE, "username");
		InputFixedFeaturesMapper inputMapper = new InputFixedFeaturesMapper(new FeaturesIndex(inputFeatures), inputMappings);

		List<String> outputFeatures = Arrays.asList("username", "call duration", "call duration mean");
		Map<String, String> outputMappings = new HashMap<>();
		outputMappings.put(MEAN_FEATURE, "call duration mean");
		OutputFeaturesMapper outputMapper = new OutputFeaturesMapper(new FeaturesIndex(outputFeatures), outputMappings);

		FixedBatchSpout spout = new FixedBatchSpout(new Fields(INSTANCE_FIELD), 1000, //
				createInputValues("pmerienne", 10), //
				createInputValues("pmerienne", 20), //
				createInputValues("pmerienne", 15), //
				createInputValues("jchanut", 13), //
				createInputValues("jchanut", 15), //
				createInputValues("jchanut", 98), //
				createInputValues("pmerienne", 10), //
				createInputValues("pmerienne", 5) //
		);
		Stream inputStream = topology.newStream("test", spout);

		DistinctFeatureMean distinctFeatureMean = new DistinctFeatureMean();
		setField(distinctFeatureMean, "in", inputStream);
		setField(distinctFeatureMean, "inputMapper", inputMapper);
		setField(distinctFeatureMean, "outputMapper", outputMapper);
		setField(distinctFeatureMean, "stateFactory", new MemoryMapState.Factory());
		distinctFeatureMean.init();

		Stream out = (Stream) getField(distinctFeatureMean, "out");
		InstanceCollector instanceCollector = new InstanceCollector();
		instanceCollector.collect(out);

		// When
		this.launch();
		Utils.sleep(TOPOLOGY_START_TIME);

		// Then
		List<Instance> collected = instanceCollector.getCollected();
		assertThat(collected).hasSize(8);

		double pmerienneExpectedMean = (10 + 20 + 15 + 10 + 5) / 5;
		double jchanutExpectedMean = (13 + 15 + 98) / 3;
		assertThat(extractMean(collected.get(0), outputMapper)).isEqualTo(pmerienneExpectedMean, delta(10e-6));
		assertThat(extractMean(collected.get(1), outputMapper)).isEqualTo(pmerienneExpectedMean, delta(10e-6));
		assertThat(extractMean(collected.get(2), outputMapper)).isEqualTo(pmerienneExpectedMean, delta(10e-6));
		assertThat(extractMean(collected.get(3), outputMapper)).isEqualTo(jchanutExpectedMean, delta(10e-6));
		assertThat(extractMean(collected.get(4), outputMapper)).isEqualTo(jchanutExpectedMean, delta(10e-6));
		assertThat(extractMean(collected.get(5), outputMapper)).isEqualTo(jchanutExpectedMean, delta(10e-6));
		assertThat(extractMean(collected.get(6), outputMapper)).isEqualTo(pmerienneExpectedMean, delta(10e-6));
		assertThat(extractMean(collected.get(7), outputMapper)).isEqualTo(pmerienneExpectedMean, delta(10e-6));
	}

	private Values createInputValues(String username, long callDuration) {
		Instance instance = instance().with(username).with(callDuration).build();
		return new Values(instance);
	}

	private double extractMean(Instance outputInstance, OutputFeaturesMapper outputMapper) {
		return outputInstance.getFeature(outputMapper, MEAN_FEATURE);
	}
}
