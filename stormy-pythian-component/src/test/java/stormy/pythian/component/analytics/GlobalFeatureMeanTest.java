package stormy.pythian.component.analytics;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Delta.delta;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static stormy.pythian.component.analytics.GlobalFeatureMean.MEAN_FEATURE;
import static stormy.pythian.component.analytics.GlobalFeatureMean.SELECTED_FEATURE;
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

public class GlobalFeatureMeanTest extends TridentIntegrationTest {

	private static final int TOPOLOGY_START_TIME = 5000;

	@SuppressWarnings("unchecked")
	@Test
	public void should_count_features_occurence() {
		// Given
		List<String> inputFeatures = Arrays.asList("firstname", "lastname", "age");
		Map<String, String> inputMappings = new HashMap<>();
		inputMappings.put(SELECTED_FEATURE, "age");
		InputFixedFeaturesMapper inputMapper = new InputFixedFeaturesMapper(new FeaturesIndex(inputFeatures), inputMappings);

		List<String> outputFeatures = Arrays.asList("firstname", "lastname", "age", "age mean");
		Map<String, String> mappings = new HashMap<>();
		mappings.put(MEAN_FEATURE, "age mean");
		OutputFeaturesMapper outputMapper = new OutputFeaturesMapper(new FeaturesIndex(outputFeatures), mappings);

		FixedBatchSpout spout = new FixedBatchSpout(new Fields(INSTANCE_FIELD), 1, //
				createInputValues("Pierre", "Merienne", 27), //
				createInputValues("Julie", "Chanut", 32), //
				createInputValues("Julie", "Poiters", 27), //
				createInputValues("Fabien", "Thouny", 27), //
				createInputValues("Fabien", "Drouin", 28), //
				createInputValues("Brive", "Duteil", 32) //
		);
		Stream inputStream = topology.newStream("test", spout);

		GlobalFeatureMean globalFeatureMean = new GlobalFeatureMean();
		setField(globalFeatureMean, "in", inputStream);
		setField(globalFeatureMean, "inputMapper", inputMapper);
		setField(globalFeatureMean, "outputMapper", outputMapper);
		setField(globalFeatureMean, "stateFactory", new MemoryMapState.Factory());
		globalFeatureMean.init();

		Stream out = (Stream) getField(globalFeatureMean, "out");
		InstanceCollector instanceCollector = new InstanceCollector();
		instanceCollector.collect(out);

		// When
		this.launch();
		Utils.sleep(TOPOLOGY_START_TIME);

		// Then
		List<Instance> collected = instanceCollector.getCollected();
		assertThat(collected).hasSize(6);
		assertThat((double) collected.get(1).getFeature(outputMapper, MEAN_FEATURE)).isEqualTo((27) / 1.0, delta(10e-6));
		assertThat((double) collected.get(2).getFeature(outputMapper, MEAN_FEATURE)).isEqualTo((27 + 32) / 2.0, delta(10e-6));
		assertThat((double) collected.get(3).getFeature(outputMapper, MEAN_FEATURE)).isEqualTo((27 + 32 + 27) / 3.0, delta(10e-6));
		assertThat((double) collected.get(4).getFeature(outputMapper, MEAN_FEATURE)).isEqualTo((27 + 32 + 27 + 27) / 4.0, delta(10e-6));
		assertThat((double) collected.get(5).getFeature(outputMapper, MEAN_FEATURE)).isEqualTo((27 + 32 + 27 + 27 + 28) / 5.0, delta(10e-6));
	}

	private Values createInputValues(String firstname, String lastName, int age) {
		Instance instance = instance().with(firstname).with(lastName).with(age).build();
		return new Values(instance);
	}
}
