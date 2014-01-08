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

import static com.google.common.collect.Sets.newHashSet;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FixedFeaturesMapperTest {

	@InjectMocks
	private FixedFeaturesMapper mapper;

	@Mock
	private Map<String, String> mappings;

	@Test
	public void should_retrieve_feature() {
		// Given
		when(mappings.get("value")).thenReturn("age");

		Instance instance = new Instance();
		instance.add("age", 32);

		// When
		Feature<Integer> actualFeature = mapper.getFeature(instance, "value");

		// Then
		assertThat(actualFeature.getValue()).isEqualTo(32);
	}

	@Test
	public void should_retrieve_null_with_no_mapping() {
		// Given
		Instance instance = new Instance();
		instance.add("age", 32);

		// When
		Feature<Integer> actualFeature = mapper.getFeature(instance, "value");

		// Then
		assertThat(actualFeature).isNull();
	}

	@Test
	public void should_retrieve_null_with_no_feature() {
		// Given
		when(mappings.get("value")).thenReturn("age");

		Instance instance = new Instance();

		// When
		Feature<Integer> actualFeature = mapper.getFeature(instance, "value");

		// Then
		assertThat(actualFeature).isNull();
	}

	@Test
	public void should_list_features() {
		// Given
		when(mappings.keySet()).thenReturn(newHashSet("dailyViews", "monthlyViews", "allTimeViews"));
		when(mappings.get("dailyViews")).thenReturn("user_daily_views");
		when(mappings.get("monthlyViews")).thenReturn("user_monthly_views");
		when(mappings.get("allTimeViews")).thenReturn("user_all_time_views");

		Instance instance = new Instance();
		instance.add("user_daily_views", 3);
		instance.add("user_all_time_views", 42);

		// When
		Map<String, Feature<?>> actualFeatures = mapper.getFeatures(instance);

		// Then
		Map<String, Feature<?>> expectedFeatures = new HashMap<>();
		expectedFeatures.put("user_daily_views", new IntegerFeature(3));
		expectedFeatures.put("user_monthly_views", null);
		expectedFeatures.put("user_all_time_views", new IntegerFeature(42));
		assertThat(actualFeatures).isEqualTo(expectedFeatures);

	}
}
