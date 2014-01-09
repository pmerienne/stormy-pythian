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
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static stormy.pythian.model.instance.Instance.Builder.*;

@RunWith(MockitoJUnitRunner.class)
public class InputFixedFeaturesMapperTest {

	@SuppressWarnings("unchecked")
	@Test
	public void should_retrieve_feature() {
		// Given
		FeaturesIndex index = mock(FeaturesIndex.class);
		Map<String, String> mappings = mock(Map.class);
		Instance instance = instance().with(new IntegerFeature(32)).build();

		when(mappings.get("value")).thenReturn("age");
		when(index.getIndex("age")).thenReturn(0);

		InputFixedFeaturesMapper mapper = new InputFixedFeaturesMapper(index, mappings);

		// When
		Feature<Integer> actualFeature = mapper.getFeature(instance, "value");

		// Then
		assertThat(actualFeature.getValue()).isEqualTo(32);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void should_retrieve_null_with_no_mapping() {
		// Given
		FeaturesIndex index = mock(FeaturesIndex.class);
		Map<String, String> mappings = mock(Map.class);
		Instance instance = instance().with(new IntegerFeature(32)).build();

		when(mappings.get("value")).thenReturn(null);

		InputFixedFeaturesMapper mapper = new InputFixedFeaturesMapper(index, mappings);

		// When
		Feature<Integer> actualFeature = mapper.getFeature(instance, "value");

		// Then
		assertThat(actualFeature).isNull();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void should_retrieve_null_with_no_feature() {
		// Given
		FeaturesIndex index = mock(FeaturesIndex.class);
		Map<String, String> mappings = mock(Map.class);
		Instance instance = instance().with(null).build();

		when(mappings.get("value")).thenReturn("age");
		when(index.getIndex("age")).thenReturn(0);

		InputFixedFeaturesMapper mapper = new InputFixedFeaturesMapper(index, mappings);

		// When
		Feature<Integer> actualFeature = mapper.getFeature(instance, "value");

		// Then
		assertThat(actualFeature).isNull();
	}

}
