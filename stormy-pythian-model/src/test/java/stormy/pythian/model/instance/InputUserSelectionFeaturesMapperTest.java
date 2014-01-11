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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class InputUserSelectionFeaturesMapperTest {

	@Test
	public void should_retrieve_selected_feature() {
		// Given
		List<String> selectedFeatures = Arrays.asList("age", "viewCount");

		FeaturesIndex index = mock(FeaturesIndex.class);
		when(index.getIndex("age")).thenReturn(0);
		when(index.getIndex("viewCount")).thenReturn(2);

		// When
		InputUserSelectionFeaturesMapper mapper = new InputUserSelectionFeaturesMapper(index, selectedFeatures);
		int[] selectedIndexes = mapper.getSelectedIndex();

		// Then
		assertThat(selectedIndexes).isEqualTo(new int[] { 0, 2 });
	}
}
