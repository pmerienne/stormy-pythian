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
package stormy.pythian.core.sandbox;

import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;

import java.util.UUID;

import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.builtin.Count;
import storm.trident.operation.builtin.Debug;
import storm.trident.operation.builtin.MapGet;
import storm.trident.testing.FixedBatchSpout;
import storm.trident.testing.MemoryMapState;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.instance.Feature;
import stormy.pythian.model.instance.Instance;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class StormSandBox {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		LocalCluster cluster = new LocalCluster();
		TridentTopology topology = new TridentTopology();
		
		try {
			FixedBatchSpout spout = new FixedBatchSpout(new Fields(INSTANCE_FIELD), 4, //
				createValues("pierre", 26), createValues("fabien", 25), createValues("meriem", 25), createValues("julie", 32), //
				createValues("brice", 32), createValues("arnaud", 30), createValues("pierre L", 30), createValues("camille", 32)//
			);
			
			Stream instances = topology.newStream("test", spout);
			
			TridentState countByAge = instances //
					.each(new Fields(Instance.INSTANCE_FIELD), new ExtractFeature("age"), new Fields("age")) //
				.groupBy(new Fields("age")) //
				.persistentAggregate(new MemoryMapState.Factory(), new Fields("age"), new Count(), new Fields("count"));
			
			countByAge.newValuesStream().each(new Fields("age", "count"), new Debug());
			
			instances //
			 .each(new Fields(Instance.INSTANCE_FIELD), new ExtractFeature("age"), new Fields("age")) //
			 .stateQuery(countByAge, new Fields("age"), new MapGet(), new Fields("age_count")) //
			 .each(new Fields(Instance.INSTANCE_FIELD, "age_count"), new Debug("final"));

			cluster.submitTopology(StormSandBox.class.getSimpleName(), new Config(), topology.build());

			Utils.sleep(120000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cluster.shutdown();
		}

	}

	private static Values createValues(String firstname, Integer age) {
		Instance instance = new Instance();
		instance.add("uuid", UUID.randomUUID().toString());
		instance.add("firstname", firstname);
		instance.add("age", age);
		return new Values(instance);
	}

	@SuppressWarnings("serial")
	private static class ExtractFeature extends BaseFunction {

		private final String featureName;

		public ExtractFeature(String featureName) {
			this.featureName = featureName;
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance instance = Instance.from(tuple);
			Feature<?> feature = instance.get(featureName);
			collector.emit(new Values(feature.getValue()));
		}

	}
}
