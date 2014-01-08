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

import java.util.Map;

import storm.trident.Stream;
import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.FeaturesMapper;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.MappingType;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.Feature;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.UserSelectionFeaturesMapper;
import backtype.storm.tuple.Fields;

@Documentation(name = "Console output")
public class ConsoleOutput implements Component {

	private static final long serialVersionUID = -3662417254204156228L;

	@InputStream(name = "in", type = MappingType.USER_SELECTION)
	private Stream in;

	@FeaturesMapper(stream = "in")
	private UserSelectionFeaturesMapper mapper;

	@Override
	public void init() {
		in.each(new Fields(INSTANCE_FIELD), new PrintToConsole(mapper));
	}

	@SuppressWarnings("serial")
	private static class PrintToConsole extends BaseFilter {

		private final UserSelectionFeaturesMapper mapper;

		public PrintToConsole(UserSelectionFeaturesMapper mapper) {
			this.mapper = mapper;
		}

		@Override
		public boolean isKeep(TridentTuple tuple) {
			Instance instance = Instance.from(tuple);
			Map<String, Feature<?>> features = mapper.getFeatures(instance);
			System.out.println(features);

			return true;
		}

	}
}
