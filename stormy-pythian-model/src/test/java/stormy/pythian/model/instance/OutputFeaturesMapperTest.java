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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;

public class OutputFeaturesMapperTest {

	@SuppressWarnings("unchecked")
	@Test
	public void should_add_feature_to_new_instance() {
		// Given
		IntegerFeature expectedFeature = new IntegerFeature(32);

		Map<String, String> mappings = mock(Map.class);
		when(mappings.get("age")).thenReturn("user age");
		FeaturesIndex index = mock(FeaturesIndex.class);
		when(index.getIndex("user age")).thenReturn(0);
		when(index.size()).thenReturn(1);

		OutputFeaturesMapper mapper = new OutputFeaturesMapper(index, mappings);

		// When
		Instance instance = mapper.newInstance().add("age", new IntegerFeature(32)).build();

		// Then
		assertThat(instance.getFeatures()[0]).isEqualTo(expectedFeature);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void should_add_feature_from_existing_instance() {
		// Given
		IntegerFeature existingFeature = new IntegerFeature(42);
		IntegerFeature expectedFeature = new IntegerFeature(32);
		Instance original = Instance.Builder.instance().with(existingFeature).build();

		Map<String, String> mappings = mock(Map.class);
		when(mappings.get("age")).thenReturn("user age");
		FeaturesIndex index = mock(FeaturesIndex.class);
		when(index.getIndex("user age")).thenReturn(1);
		when(index.size()).thenReturn(2);

		OutputFeaturesMapper mapper = new OutputFeaturesMapper(index, mappings);

		// When
		Instance instance = mapper.from(original).add("age", new IntegerFeature(32)).build();

		// Then
		assertThat(instance.getFeatures()[1]).isEqualTo(expectedFeature);
		assertThat(instance.getFeatures()[0]).isEqualTo(existingFeature);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IllegalArgumentException.class)
	public void should_fail_with_unknown_feature() {
		// Given
		Map<String, String> mappings = mock(Map.class);
		when(mappings.get("age")).thenReturn(null);
		FeaturesIndex index = mock(FeaturesIndex.class);

		OutputFeaturesMapper mapper = new OutputFeaturesMapper(index, mappings);

		// When
		mapper.newInstance().add("age", new IntegerFeature(32)).build();
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IllegalArgumentException.class)
	public void should_fail_when_no_feature_index() {
		// Given
		Map<String, String> mappings = mock(Map.class);
		when(mappings.get("age")).thenReturn("user age");
		FeaturesIndex index = mock(FeaturesIndex.class);
		when(index.getIndex("user age")).thenReturn(-1);

		OutputFeaturesMapper mapper = new OutputFeaturesMapper(index, mappings);

		// When
		mapper.newInstance().add("age", new IntegerFeature(32)).build();
	}
}
