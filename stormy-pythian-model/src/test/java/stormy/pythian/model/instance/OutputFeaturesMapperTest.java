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
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OutputFeaturesMapperTest {

	@InjectMocks
	private OutputFeaturesMapper mapper;

	@Mock
	private Map<String, String> mappings;

	@Mock
	private FeaturesIndex featuresIndex;

	@Test
	public void should_retrieve_feature_index() {
		// Given
		when(mappings.get("count")).thenReturn("view count");
		when(featuresIndex.getIndex("view count")).thenReturn(3);

		// When
		int actualIndex = mapper.getFeatureIndex("count");

		// Then
		assertThat(actualIndex).isEqualTo(3);
	}

	@Test
	public void should_retrieve_minus_one_with_no_mapping() {
		// Given
		when(mappings.get("count")).thenReturn(null);

		// When
		int actualIndex = mapper.getFeatureIndex("count");

		// Then
		assertThat(actualIndex).isEqualTo(-1);
	}

	@Test
	public void should_retrieve_minus_one_when_feature_not_in_index() {
		// Given
		when(mappings.get("count")).thenReturn("view count");
		when(featuresIndex.getIndex("view count")).thenReturn(-1);

		// When
		int actualIndex = mapper.getFeatureIndex("count");

		// Then
		assertThat(actualIndex).isEqualTo(-1);
	}
}
