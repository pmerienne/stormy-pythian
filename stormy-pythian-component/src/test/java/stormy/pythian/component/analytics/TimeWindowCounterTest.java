package stormy.pythian.component.analytics;

import static org.apache.commons.lang.time.DateUtils.MILLIS_PER_DAY;
import static org.apache.commons.lang.time.DateUtils.MILLIS_PER_HOUR;
import static org.apache.commons.lang.time.DateUtils.addHours;
import static org.fest.assertions.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static stormy.pythian.component.analytics.TimeWindowCounter.COUNT_FEATURE;
import static stormy.pythian.component.analytics.TimeWindowCounter.DATE_FEATURE;
import static stormy.pythian.component.analytics.TimeWindowCounter.SELECTED_FEATURE;
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
import stormy.pythian.model.instance.FeaturesIndex;
import stormy.pythian.model.instance.InputFixedFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFeaturesMapper;
import stormy.pythian.testing.InstanceCollector;
import stormy.pythian.testing.TridentIntegrationTest;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;


public class TimeWindowCounterTest extends TridentIntegrationTest {

	private static final int TOPOLOGY_START_TIME = 5000;

	@SuppressWarnings("unchecked")
	@Test
	public void should_count_features_occurence() {
		// Given
		List<String> inputFeatures = Arrays.asList("username", "call date");
		
		Map<String, String> inputMappings = new HashMap<>();
		inputMappings.put(SELECTED_FEATURE, "username");
		inputMappings.put(DATE_FEATURE, "call date");
		InputFixedFeaturesMapper inputMapper = new InputFixedFeaturesMapper(new FeaturesIndex(inputFeatures), inputMappings );

		List<String> outputFeatures = Arrays.asList("username", "call date", "last day call's count");
		Map<String, String> mappings = new HashMap<>();
		mappings.put(COUNT_FEATURE, "last day call's count");
		OutputFeaturesMapper outputMapper = new OutputFeaturesMapper(new FeaturesIndex(outputFeatures), mappings);
		
		Date now = new Date();
		long thirtyHoursAgo = addHours(now, -30).getTime();
		long twelveHoursAgo = addHours(now, -12).getTime();
		long anHourAgo = addHours(now, -1).getTime();

		FixedBatchSpout spout = new FixedBatchSpout(new Fields(INSTANCE_FIELD), 1000, //
				createValues("pmerienne", thirtyHoursAgo), //
				
				createValues("pmerienne", twelveHoursAgo), //
				createValues("jchanut", twelveHoursAgo), //

				createValues("pmerienne", anHourAgo), //
				createValues("jchanut", anHourAgo) //
		);
		Stream inputStream = topology.newStream("test", spout);

		TimeWindowCounter counter = new TimeWindowCounter();
		setField(counter, "in", inputStream);
		setField(counter, "inputMapper", inputMapper);
		setField(counter, "outputMapper", outputMapper);
		setField(counter, "stateFactory", new MemoryMapState.Factory());
		setField(counter, "windowLengthMs", MILLIS_PER_DAY);
		setField(counter, "slotLengthMs", MILLIS_PER_HOUR);
		counter.init();

		Stream out = (Stream) getField(counter, "out");
		InstanceCollector instanceCollector = new InstanceCollector();
		instanceCollector.collect(out);

		// When
		this.launch();
		Utils.sleep(TOPOLOGY_START_TIME);
		System.out.println(instanceCollector.getCollected());
		

		// Then
		assertThat(instanceCollector.getCollected()).contains( //
			createInstance("pmerienne", thirtyHoursAgo, 1), //
			
			createInstance("pmerienne", twelveHoursAgo, 2), //
			createInstance("jchanut", twelveHoursAgo, 1), //

			createInstance("pmerienne", anHourAgo, 2), //
			createInstance("jchanut", anHourAgo, 2) //
		);
	}

	private Values createValues(String username, long callDate) {
		Instance instance = instance() //
				.with(username) //
				.with(callDate) //
				.build();
		return new Values(instance);
	}

	private Instance createInstance(String username, long callDate, long lastDayCallsCount) {
		return instance() //
				.with(username) //
				.with(callDate) //
				.with(lastDayCallsCount) //
				.build();
	}
}
