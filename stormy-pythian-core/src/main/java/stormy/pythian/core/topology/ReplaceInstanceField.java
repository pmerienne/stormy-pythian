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
package stormy.pythian.core.topology;

import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import static stormy.pythian.model.instance.Instance.NEW_INSTANCE_FIELD;
import storm.trident.Stream;
import storm.trident.operation.Assembly;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class ReplaceInstanceField implements Assembly {

	@Override
	public Stream apply(Stream input) {
		return input //
				.project(new Fields(NEW_INSTANCE_FIELD)) //
				.each(new Fields(NEW_INSTANCE_FIELD), new Duplicate(), new Fields(INSTANCE_FIELD)) //
				.project(new Fields(INSTANCE_FIELD));
	}

	private static class Duplicate extends BaseFunction {

		private static final long serialVersionUID = 4238149014859176374L;

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Object field = tuple.getValue(0);
			collector.emit(new Values(field));
		}

	}
}
