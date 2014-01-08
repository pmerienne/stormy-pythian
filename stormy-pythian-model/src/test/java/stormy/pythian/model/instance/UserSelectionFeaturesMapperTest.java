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

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class UserSelectionFeaturesMapperTest {

	private UserSelectionFeaturesMapper mapper;

	@Before
	public void init() {
		List<String> selectedFeatures = Arrays.asList("age", "viewCount");
		mapper = new UserSelectionFeaturesMapper(selectedFeatures);
	}

	@Test
	public void should_retrieve_selected_feature() {
		// Given
		Instance instance = new Instance();
		instance.add("age", 32);
		instance.add("viewCount", 42);

		// When
		Map<String, Feature<?>> actualsFeatures = mapper.getFeatures(instance);

		// Then
		assertThat(actualsFeatures).includes(entry("age", new IntegerFeature(32)), entry("viewCount", new IntegerFeature(42)));
	}

	@Test
	public void should_retrieve_null_when_no_feature() {
		// Given
		Instance instance = new Instance();
		instance.add("viewCount", 42);

		// When
		Map<String, Feature<?>> actualsFeatures = mapper.getFeatures(instance);

		// Then
		Map<String, Feature<?>> expectedFeatures = new HashMap<>();
		expectedFeatures.put("age", null);
		expectedFeatures.put("viewCount", new IntegerFeature(42));
		assertThat(actualsFeatures).isEqualTo(expectedFeatures);
	}

	@Test
	public void should_retrieve_feature_by_name() {
		// Given
		Instance instance = new Instance();
		instance.add("age", 32);
		instance.add("viewCount", 42);

		// When
		Feature<Object> actualFeature = mapper.getFeature(instance, "viewCount");

		// Then
		assertThat(actualFeature.getValue()).isEqualTo(42);

	}
}
