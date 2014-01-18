package stormy.pythian.component.analytics;

import static org.fest.assertions.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static stormy.pythian.component.analytics.GlobalFeatureQuantiles.QUANTILE_95th_FEATURE;
import static stormy.pythian.component.analytics.GlobalFeatureQuantiles.QUANTILE_99th9_FEATURE;
import static stormy.pythian.component.analytics.GlobalFeatureQuantiles.QUANTILE_99th_FEATURE;
import static stormy.pythian.component.analytics.GlobalFeatureQuantiles.QUANTILE_90th_FEATURE;
import static stormy.pythian.component.analytics.GlobalFeatureQuantiles.MEDIAN_FEATURE;
import static stormy.pythian.component.analytics.GlobalFeatureQuantiles.QUANTILE_75th_FEATURE;
import static stormy.pythian.component.analytics.GlobalFeatureQuantiles.SELECTED_FEATURE;
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



public class GlobalFeatureQuantilesTest extends TridentIntegrationTest {

	private static final int TOPOLOGY_START_TIME = 5000;

	@SuppressWarnings("unchecked")
	@Test
	public void should_compute_features_quantiles() {
		// Given
		List<String> inputFeatures = Arrays.asList("firstname", "lastname", "age");
		Map<String, String> inputMappings = new HashMap<>();
		inputMappings.put(SELECTED_FEATURE, "age");
		InputFixedFeaturesMapper inputMapper = new InputFixedFeaturesMapper(new FeaturesIndex(inputFeatures), inputMappings);

		List<String> outputFeatures = Arrays.asList("firstname", "lastname", "age", "median", "75th percentiles", "90th percentiles", "95th percentiles", "99th percentiles", "99.9th percentiles");
		Map<String, String> mappings = new HashMap<>();
		mappings.put(MEDIAN_FEATURE, "median");
		mappings.put(QUANTILE_75th_FEATURE, "75th percentiles");
		mappings.put(QUANTILE_90th_FEATURE, "90th percentiles");
		mappings.put(QUANTILE_95th_FEATURE, "95th percentiles");
		mappings.put(QUANTILE_99th_FEATURE, "99th percentiles");
		mappings.put(QUANTILE_99th9_FEATURE, "99.9th percentiles");
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

		GlobalFeatureQuantiles component = new GlobalFeatureQuantiles();
		setField(component, "in", inputStream);
		setField(component, "inputMapper", inputMapper);
		setField(component, "outputMapper", outputMapper);
		setField(component, "stateFactory", new MemoryMapState.Factory());
		component.init();

		Stream out = (Stream) getField(component, "out");
		InstanceCollector instanceCollector = new InstanceCollector();
		instanceCollector.collect(out);

		// When
		this.launch();
		Utils.sleep(TOPOLOGY_START_TIME);

		// Then
		List<Instance> collected = instanceCollector.getCollected();
		assertThat(collected).hasSize(6);
		System.out.println(collected);
	}

	private Values createInputValues(String firstname, String lastName, int age) {
		Instance instance = instance().with(firstname).with(lastName).with(age).build();
		return new Values(instance);
	}
}
