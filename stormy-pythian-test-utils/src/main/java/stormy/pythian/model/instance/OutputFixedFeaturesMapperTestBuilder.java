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
package stormy.pythian.model.instance;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputFixedFeaturesMapperTestBuilder {

	private final List<String> features = new ArrayList<>();
	private final Map<String, String> mappings = new HashMap<>();

	public static OutputFixedFeaturesMapperTestBuilder outputFixedFeaturesMapper() {
		return new OutputFixedFeaturesMapperTestBuilder();
	}

	public static OutputFixedFeaturesMapperTestBuilder outputFixedFeaturesMapper(String... features) {
		OutputFixedFeaturesMapperTestBuilder builder = new OutputFixedFeaturesMapperTestBuilder();
		return builder.with(features);
	}

	public OutputFixedFeaturesMapperTestBuilder with(String... features) {
		this.features.addAll(asList(features));
		return this;
	}

	public OutputFixedFeaturesMapperTestBuilder map(String componentFeatureName, String instanceFeatureName) {
		this.mappings.put(componentFeatureName, instanceFeatureName);
		return this;
	}

	public OutputFixedFeaturesMapper build() {
		return new OutputFixedFeaturesMapper(new FeaturesIndex(features), mappings);
	}
}
