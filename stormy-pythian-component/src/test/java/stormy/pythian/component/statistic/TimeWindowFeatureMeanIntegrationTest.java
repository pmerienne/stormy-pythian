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

import static org.apache.commons.lang.time.DateUtils.MILLIS_PER_DAY;
import static org.apache.commons.lang.time.DateUtils.MILLIS_PER_HOUR;
import static org.apache.commons.lang.time.DateUtils.addHours;
import static org.fest.assertions.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static stormy.pythian.component.statistic.aggregation.Constants.COMPUTED_FEATURE;
import static stormy.pythian.component.statistic.aggregation.Constants.DATE_FEATURE;
import static stormy.pythian.component.statistic.aggregation.Constants.GROUP_BY_FEATURE;
import static stormy.pythian.component.statistic.aggregation.Constants.MEAN_FEATURE;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import static stormy.pythian.model.instance.InstanceTestBuilder.instance;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import storm.trident.Stream;
import storm.trident.testing.FixedBatchSpout;
import storm.trident.testing.MemoryMapState;
import stormy.pythian.component.statistic.TimeWindowFeatureMean;
import stormy.pythian.model.instance.FeaturesIndex;
import stormy.pythian.model.instance.InputFixedFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFixedFeaturesMapper;
import stormy.pythian.testing.InstanceCollector;
import stormy.pythian.testing.TridentIntegrationTest;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class TimeWindowFeatureMeanIntegrationTest extends TridentIntegrationTest {

	private static final int TOPOLOGY_START_TIME = 5000;

	@SuppressWarnings("unchecked")
	@Test
	public void should_compute_each_user_call_duration_mean_during_last_day() {
		// Given
		List<String> inputFeatures = Arrays.asList("username", "call date", "call duration");

		Map<String, String> inputMappings = new HashMap<>();
		inputMappings.put(GROUP_BY_FEATURE, "username");
		inputMappings.put(DATE_FEATURE, "call date");
		inputMappings.put(COMPUTED_FEATURE, "call duration");
		InputFixedFeaturesMapper inputMapper = new InputFixedFeaturesMapper(new FeaturesIndex(inputFeatures), inputMappings);

		List<String> outputFeatures = Arrays.asList("username", "call date", "call duration", "last day call's duration mean");
		Map<String, String> mappings = new HashMap<>();
		mappings.put(MEAN_FEATURE, "last day call's duration mean");
		OutputFixedFeaturesMapper outputMapper = new OutputFixedFeaturesMapper(new FeaturesIndex(outputFeatures), mappings);

		Date now = new Date();
		long thirtyHoursAgo = addHours(now, -30).getTime();
		long twelveHoursAgo = addHours(now, -12).getTime();
		long anHourAgo = addHours(now, -1).getTime();

		FixedBatchSpout spout = new FixedBatchSpout(new Fields(INSTANCE_FIELD), 1000, //
				createInputValues("pmerienne", thirtyHoursAgo, 200), //

				createInputValues("pmerienne", twelveHoursAgo, 100), //
				createInputValues("jchanut", twelveHoursAgo, 150), //

				createInputValues("pmerienne", anHourAgo, 100), //
				createInputValues("jchanut", anHourAgo, 100) //
		);
		Stream inputStream = topology.newStream("test", spout);

		TimeWindowFeatureMean component = new TimeWindowFeatureMean();
		setField(component, "in", inputStream);
		setField(component, "inputMapper", inputMapper);
		setField(component, "outputMapper", outputMapper);
		setField(component, "stateFactory", new MemoryMapState.Factory());
		setField(component, "windowLengthMs", MILLIS_PER_DAY);
		setField(component, "slotLengthMs", MILLIS_PER_HOUR);
		component.init();

		Stream out = (Stream) getField(component, "out");
		InstanceCollector instanceCollector = new InstanceCollector();
		instanceCollector.collect(out);

		// When
		this.launch();
		Utils.sleep(TOPOLOGY_START_TIME);
		System.out.println(instanceCollector.getCollected());

		// Then
		assertThat(instanceCollector.getCollected()).contains( //
				createOutputInstance("pmerienne", thirtyHoursAgo, 200, 200.0), //

				createOutputInstance("pmerienne", twelveHoursAgo, 100, 150.0), //
				createOutputInstance("jchanut", twelveHoursAgo, 150, 150.0), //

				createOutputInstance("pmerienne", anHourAgo, 100, 100.0), //
				createOutputInstance("jchanut", anHourAgo, 100, 125.0) //
				);
	}

	private Values createInputValues(String username, long callDate, long callDuration) {
		Instance instance = instance().with(username).with(callDate).with(callDuration).build();
		return new Values(instance);
	}

	private Instance createOutputInstance(String username, long callDate, long callDuration, double lastDayCallsDurationMean) {
		return instance().with(username).with(callDate).with(callDuration).with(lastDayCallsDurationMean).build();
	}

}
