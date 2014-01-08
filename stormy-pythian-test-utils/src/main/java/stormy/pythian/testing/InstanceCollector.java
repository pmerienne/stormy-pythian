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
package stormy.pythian.testing;

import static java.util.UUID.randomUUID;
import static stormy.pythian.model.instance.Instance.NEW_INSTANCE_FIELD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import storm.trident.Stream;
import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.instance.Instance;
import backtype.storm.tuple.Fields;

public class InstanceCollector extends BaseFilter {

	private static final long serialVersionUID = 4534166735819718393L;

	private final static Map<String, List<Instance>> COLLECTED = new HashMap<>();
	private final String uuid;

	public InstanceCollector() {
		this.uuid = randomUUID().toString();
		COLLECTED.put(uuid, new ArrayList<Instance>());
	}

	public List<Instance> getCollected() {
		return COLLECTED.get(uuid);
	}

	@Override
	public boolean isKeep(TridentTuple tuple) {
		Instance instance = (Instance) tuple.getValueByField(NEW_INSTANCE_FIELD);
		getCollected().add(instance);
		return true;
	}

	public void listen(Stream stream) {
		stream.each(new Fields(NEW_INSTANCE_FIELD), this);
	}

}
