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

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Delta.delta;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static stormy.pythian.component.statistic.aggregation.Constants.COMPUTED_FEATURE;
import static stormy.pythian.component.statistic.aggregation.Constants.MEAN_FEATURE;
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
import stormy.pythian.component.statistic.GlobalMean;
import stormy.pythian.model.instance.FeaturesIndex;
import stormy.pythian.model.instance.InputFixedFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFeaturesMapper;
import stormy.pythian.testing.InstanceCollector;
import stormy.pythian.testing.TridentIntegrationTest;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class GlobalMeanTest extends TridentIntegrationTest {

	private static final int TOPOLOGY_START_TIME = 5000;

	@SuppressWarnings("unchecked")
	@Test
	public void should_compute_feature_mean() {
		// Given
		List<String> inputFeatures = Arrays.asList("firstname", "lastname", "age");
		Map<String, String> inputMappings = new HashMap<>();
		inputMappings.put(COMPUTED_FEATURE, "age");
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

		GlobalMean globalFeatureMean = new GlobalMean();
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
