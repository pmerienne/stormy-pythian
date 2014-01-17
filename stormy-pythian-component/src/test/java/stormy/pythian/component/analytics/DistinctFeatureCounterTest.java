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

import static org.fest.assertions.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static stormy.pythian.component.analytics.DistinctFeatureCounter.COUNT_FEATURE;
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
import stormy.pythian.model.instance.InputUserSelectionFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFeaturesMapper;
import stormy.pythian.testing.InstanceCollector;
import stormy.pythian.testing.TridentIntegrationTest;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class DistinctFeatureCounterTest extends TridentIntegrationTest {

	private static final int TOPOLOGY_START_TIME = 5000;

	@SuppressWarnings("unchecked")
	@Test
	public void should_count_features_occurence() {
		// Given
		List<String> inputFeatures = Arrays.asList("firstname", "lastname", "age");
		List<String> selectedFeatures = Arrays.asList("age");
		InputUserSelectionFeaturesMapper inputMapper = new InputUserSelectionFeaturesMapper(new FeaturesIndex(inputFeatures), selectedFeatures);

		List<String> outputFeatures = Arrays.asList("firstname", "lastname", "age", "count by age");
		Map<String, String> mappings = new HashMap<>();
		mappings.put(COUNT_FEATURE, "count by age");
		OutputFeaturesMapper outputMapper = new OutputFeaturesMapper(new FeaturesIndex(outputFeatures), mappings);

		FixedBatchSpout spout = new FixedBatchSpout(new Fields(INSTANCE_FIELD), 1000, //
				createValues("Pierre", "Merienne", 27), //
				createValues("Julie", "Chanut", 32), //
				createValues("Julie", "Poiters", 27), //
				createValues("Fabien", "Thouny", 27), //
				createValues("Fabien", "Drouin", 28), //
				createValues("Brive", "Duteil", 32) //
		);
		Stream inputStream = topology.newStream("test", spout);

		DistinctFeatureCounter counter = new DistinctFeatureCounter();
		setField(counter, "in", inputStream);
		setField(counter, "inputMapper", inputMapper);
		setField(counter, "outputMapper", outputMapper);
		setField(counter, "stateFactory", new MemoryMapState.Factory());
		counter.init();

		Stream out = (Stream) getField(counter, "out");
		InstanceCollector instanceCollector = new InstanceCollector();
		instanceCollector.collect(out);

		// When
		this.launch();
		Utils.sleep(TOPOLOGY_START_TIME);

		// Then
		assertThat(instanceCollector.getCollected()).contains( //
				createInstance("Pierre", "Merienne", 27, 3), //
				createInstance("Julie", "Chanut", 32, 2), //
				createInstance("Julie", "Poiters", 27, 3), //
				createInstance("Fabien", "Thouny", 27, 3), //
				createInstance("Fabien", "Drouin", 28, 1), //
				createInstance("Brive", "Duteil", 32, 2) //
				);
	}

	private Values createValues(String firstname, String lastName, int age) {
		Instance instance = createInstance(firstname, lastName, age);
		return new Values(instance);
	}

	private Instance createInstance(String firstname, String lastName, int age) {
		return instance() //
				.with(firstname) //
				.with(lastName) //
				.with(age) //
				.build();
	}

	private Instance createInstance(String firstname, String lastName, int age, long countByAge) {
		return instance() //
				.with(firstname) //
				.with(lastName) //
				.with(age) //
				.with(countByAge) //
				.build();
	}
}
