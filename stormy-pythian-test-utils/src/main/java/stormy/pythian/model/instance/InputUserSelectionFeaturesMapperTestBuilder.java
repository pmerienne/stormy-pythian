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
import java.util.List;

public class InputUserSelectionFeaturesMapperTestBuilder {

	private final List<String> features = new ArrayList<>();
	private final List<String> selected = new ArrayList<>();

	public static InputUserSelectionFeaturesMapperTestBuilder inputFixedFeaturesMapper() {
		return new InputUserSelectionFeaturesMapperTestBuilder();
	}

	public static InputUserSelectionFeaturesMapperTestBuilder inputUserSelectionFeaturesMapper(String... features) {
		InputUserSelectionFeaturesMapperTestBuilder builder = new InputUserSelectionFeaturesMapperTestBuilder();
		return builder.with(features);
	}

	public InputUserSelectionFeaturesMapperTestBuilder with(String... features) {
		this.features.addAll(asList(features));
		return this;
	}

	public InputUserSelectionFeaturesMapperTestBuilder select(String... features) {
		this.selected.addAll(asList(features));
		return this;
	}

	public InputUserSelectionFeaturesMapper build() {
		return new InputUserSelectionFeaturesMapper(new FeaturesIndex(features), selected);
	}
}
