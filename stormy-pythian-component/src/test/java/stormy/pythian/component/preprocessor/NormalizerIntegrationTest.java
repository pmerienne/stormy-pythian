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
package stormy.pythian.component.preprocessor;

import static java.lang.Math.abs;
import static org.apache.commons.lang.math.RandomUtils.nextDouble;
import static org.fest.assertions.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import storm.trident.Stream;
import storm.trident.testing.FixedBatchSpout;
import stormy.pythian.model.instance.FeaturesIndex;
import stormy.pythian.model.instance.InputUserSelectionFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.InstanceTestBuilder;
import stormy.pythian.testing.InstanceCollector;
import stormy.pythian.testing.TridentIntegrationTest;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class NormalizerIntegrationTest extends TridentIntegrationTest {
	//
	private static final int TOPOLOGY_START_TIME = 5000;

	@SuppressWarnings("unchecked")
	@Test
	public void should_normalize_doubles_features() {
		// Given
		List<String> features = Arrays.asList("f1", "f2", "f3", "f4", "f5", "f6");
		InputUserSelectionFeaturesMapper mapper = new InputUserSelectionFeaturesMapper(new FeaturesIndex(features), features);

		FixedBatchSpout spout = new FixedBatchSpout(new Fields(INSTANCE_FIELD), 3, //
				createValues(features.size()), //
				createValues(features.size()), //
				createValues(features.size()), //
				createValues(features.size()), //
				createValues(features.size()), //
				createValues(features.size()) //
		);
		Stream inputStream = topology.newStream("test", spout);

		Normalizer normalizer = new Normalizer();
		setField(normalizer, "mapper", mapper);
		setField(normalizer, "in", inputStream);
		normalizer.init();
		Stream out = (Stream) getField(normalizer, "out");

		InstanceCollector instanceCollector = new InstanceCollector();
		instanceCollector.collect(out);

		// When
		this.launch();
		Utils.sleep(TOPOLOGY_START_TIME);

		// Then
		List<Instance> actualInstances = instanceCollector.getCollected();
		for (Instance instance : actualInstances) {
			Object[] actualFeatures = instance.getSelectedFeatures(mapper);
			assertThat(actualFeatures).hasSize(features.size());
			assertNormalized(actualFeatures);
		}
	}

	private void assertNormalized(Object[] actualFeatures) {
		assertThat(actualFeatures).isNotNull();

		for (int i = 0; i < actualFeatures.length; i++) {
			assertThat(abs((Double) actualFeatures[i])).isLessThan(1.0);
		}
	}

	private Values createValues(int size) {
		List<Object> features = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			features.add(nextDouble() * 10);
		}

		Instance instance = InstanceTestBuilder.instance().withAll(features).build();
		return new Values(instance);
	}

}