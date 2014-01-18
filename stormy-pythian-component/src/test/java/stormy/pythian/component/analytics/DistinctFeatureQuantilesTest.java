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

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static stormy.pythian.component.analytics.DistinctFeatureQuantiles.GROUP_BY_FEATURE;
import static stormy.pythian.component.analytics.DistinctFeatureQuantiles.QUANTILES_ON_FEATURE;
import static stormy.pythian.component.analytics.DistinctFeatureQuantiles.QUANTILE_95th_FEATURE;
import static stormy.pythian.component.analytics.GlobalFeatureQuantiles.MEDIAN_FEATURE;
import static stormy.pythian.component.analytics.GlobalFeatureQuantiles.QUANTILE_75th_FEATURE;
import static stormy.pythian.component.analytics.GlobalFeatureQuantiles.QUANTILE_90th_FEATURE;
import static stormy.pythian.component.analytics.GlobalFeatureQuantiles.QUANTILE_99th9_FEATURE;
import static stormy.pythian.component.analytics.GlobalFeatureQuantiles.QUANTILE_99th_FEATURE;
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

public class DistinctFeatureQuantilesTest extends TridentIntegrationTest {

	private static final int TOPOLOGY_START_TIME = 5000;

	@SuppressWarnings("unchecked")
	@Test
	public void should_compute_feature_quantiles() {
		// Given
		List<String> inputFeatures = Arrays.asList("path", "response time");
		Map<String, String> inputMappings = new HashMap<>();
		inputMappings.put(GROUP_BY_FEATURE, "path");
		inputMappings.put(QUANTILES_ON_FEATURE, "response time");
		InputFixedFeaturesMapper inputMapper = new InputFixedFeaturesMapper(new FeaturesIndex(inputFeatures), inputMappings);

		List<String> outputFeatures = Arrays.asList("path", "response time", "median", "75th percentiles", "90th percentiles", "95th percentiles", "99th percentiles", "99.9th percentiles");
		Map<String, String> outputMappings = new HashMap<>();
		outputMappings.put(MEDIAN_FEATURE, "median");
		outputMappings.put(QUANTILE_75th_FEATURE, "75th percentiles");
		outputMappings.put(QUANTILE_90th_FEATURE, "90th percentiles");
		outputMappings.put(QUANTILE_95th_FEATURE, "95th percentiles");
		outputMappings.put(QUANTILE_99th_FEATURE, "99th percentiles");
		outputMappings.put(QUANTILE_99th9_FEATURE, "99.9th percentiles");
		OutputFeaturesMapper outputMapper = new OutputFeaturesMapper(new FeaturesIndex(outputFeatures), outputMappings);

		FixedBatchSpout spout = new FixedBatchSpout(new Fields(INSTANCE_FIELD), 1, //
				createInputValues("GET /movies/{id}/ratings/mean", nextInt(100)), //
				createInputValues("POST /movies/{id}/ratings", nextInt(500)), //
				createInputValues("GET /movies/{id}/name", nextInt(50)), //
				createInputValues("GET /movies/{id}/ratings/mean", nextInt(100)), //
				createInputValues("GET /movies/{id}/name", nextInt(50)), //
				createInputValues("GET /movies/{id}/ratings/mean", nextInt(100)), //
				createInputValues("GET /movies/{id}/name", nextInt(50)), //
				createInputValues("GET /movies/{id}/ratings/mean", nextInt(100)), //
				createInputValues("GET /movies/{id}/name", nextInt(50)), //
				createInputValues("POST /movies/{id}/ratings", nextInt(500)), //
				createInputValues("POST /movies/{id}/ratings", nextInt(500)) //
		);
		Stream inputStream = topology.newStream("test", spout);

		DistinctFeatureQuantiles compute = new DistinctFeatureQuantiles();
		setField(compute, "in", inputStream);
		setField(compute, "inputMapper", inputMapper);
		setField(compute, "outputMapper", outputMapper);
		setField(compute, "stateFactory", new MemoryMapState.Factory());
		compute.init();

		Stream out = (Stream) getField(compute, "out");
		InstanceCollector instanceCollector = new InstanceCollector();
		instanceCollector.collect(out);

		// When
		this.launch();
		Utils.sleep(TOPOLOGY_START_TIME);

		// Then
		List<Instance> collected = instanceCollector.getCollected();
		System.out.println(collected);
	}

	private Values createInputValues(String path, int responseTime) {
		Instance instance = instance().with(path).with(responseTime).build();
		return new Values(instance);
	}
}
