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

import static stormy.pythian.model.annotation.MappingType.USER_SELECTION;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import storm.trident.Stream;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.Mapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.InputUserSelectionFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@SuppressWarnings("serial")
public abstract class PreProcessor implements Component {

	@InputStream(name = "in", type = USER_SELECTION)
	private transient Stream in;

	@OutputStream(name = "out")
	private transient Stream out;

	@Mapper(stream = "in")
	protected InputUserSelectionFeaturesMapper mapper;

	@Override
	public void init() {
		out = in.each(new Fields(INSTANCE_FIELD), new PreProcessingFunction(this), new Fields(Instance.NEW_INSTANCE_FIELD));
	}

	protected abstract Instance process(Instance original);

	private static class PreProcessingFunction extends BaseFunction {

		private final PreProcessor processor;

		public PreProcessingFunction(PreProcessor processor) {
			this.processor = processor;
		}

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance original = Instance.from(tuple);
			Instance newInstance = processor.process(original);
			collector.emit(new Values(newInstance));
		}

	}
}
