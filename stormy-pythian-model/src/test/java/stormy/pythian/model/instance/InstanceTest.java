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
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InstanceTest {

	@Mock
	private InputFixedFeaturesMapper inputFixedFeaturesMapper;

	@Mock
	private InputUserSelectionFeaturesMapper inputUserSelectionFeaturesMapper;

	@Mock
	private OutputFixedFeaturesMapper outputFixedFeaturesMapper;

	@Mock
	private OutputUserSelectionFeaturesMapper outputUserSelectionFeaturesMapper;

	@Test
	public void should_get_feature_by_name() {
		// Given
		Instance instance = createUnlabelledInstance("Patrick", "Star", 32);

		String featureName = "age";
		when(inputFixedFeaturesMapper.getFeatureIndex("age")).thenReturn(2);

		// When
		Integer age = instance.getInputFeature(inputFixedFeaturesMapper, featureName);

		// Then
		assertThat(age).isEqualTo(32);
	}

	@Test
	public void should_get_null_feature_if_feature_does_not_exist() {
		// Given
		Instance instance = createUnlabelledInstance("Patrick", "Star", 32);

		String featureName = "age";
		when(inputFixedFeaturesMapper.getFeatureIndex("age")).thenReturn(-1);

		// When
		Integer age = instance.getInputFeature(inputFixedFeaturesMapper, featureName);

		// Then
		assertThat(age).isNull();
	}

	@Test
	public void should_get_selected_features() {
		// Given
		Instance instance = createUnlabelledInstance("Patrick", "Star", 32);
		when(inputUserSelectionFeaturesMapper.getSelectedIndex()).thenReturn(new int[] { 0, 2 });

		// When
		Object[] selectedFeatures = instance.getSelectedFeatures(inputUserSelectionFeaturesMapper);

		// Then
		assertThat(selectedFeatures).isEqualTo(new Object[] { "Patrick", 32 });

	}

	@SuppressWarnings("serial")
	@Test
	public void should_apply_procedure() {
		// Given
		Instance instance = createUnlabelledInstance("Patrick", "Star", 32);
		when(inputUserSelectionFeaturesMapper.getSelectedIndex()).thenReturn(new int[] { 0, 1 });

		final StringBuilder sb = new StringBuilder();
		FeatureProcedure<String> procedure = new FeatureProcedure<String>() {
			@Override
			public void process(String feature) {
				sb.append(feature);
			}
		};

		// When
		instance.process(inputUserSelectionFeaturesMapper, procedure);

		// Then
		assertThat(sb.toString()).isEqualTo("PatrickStar");
	}

	@SuppressWarnings("serial")
	@Test
	public void should_apply_function() {
		// Given
		Instance instance = createUnlabelledInstance("Patrick", "Star", 32);
		when(inputUserSelectionFeaturesMapper.getSelectedIndex()).thenReturn(new int[] { 0, 1 });

		FeatureFunction<String> function = new FeatureFunction<String>() {
			@Override
			public String transform(String feature) {
				return feature.toLowerCase();
			}
		};

		// When
		Instance newInstance = instance.transform(inputUserSelectionFeaturesMapper, function);

		// Then
		assertThat(newInstance).isEqualTo(createUnlabelledInstance("patrick", "star", 32));
	}

	@Test
	public void should_set_feature_with_input_mapper() {
		// Given
		Instance instance = createUnlabelledInstance("Patrick", "Star", 26);
		when(inputFixedFeaturesMapper.getFeatureIndex("age")).thenReturn(2);

		// When
		Instance newInstance = instance.withFeature(inputFixedFeaturesMapper, "age", 32);

		// Then
		assertThat(newInstance).isEqualTo(createUnlabelledInstance("Patrick", "Star", 32));
	}

	@Test
	public void should_set_features_with_input_mapper() {
		// Given
		Instance instance = createUnlabelledInstance("Patrick", "star", 27);
		when(inputFixedFeaturesMapper.getFeatureIndex("name")).thenReturn(1);
		when(inputFixedFeaturesMapper.getFeatureIndex("age")).thenReturn(2);

		Map<String, Object> features = new HashMap<>();
		features.put("age", 32);
		features.put("name", "Star");

		// When
		Instance newInstance = instance.withFeatures(inputFixedFeaturesMapper, features);

		// Then
		assertThat(newInstance).isEqualTo(createUnlabelledInstance("Patrick", "Star", 32));
	}

	@Test
	public void should_set_feature_with_fixed_output_mapper() {
		// Given
		Instance instance = createUnlabelledInstance("Patrick", "Star");
		when(outputFixedFeaturesMapper.size()).thenReturn(3);
		when(outputFixedFeaturesMapper.getFeatureIndex("age")).thenReturn(2);

		// When
		Instance newInstance = instance.withFeature(outputFixedFeaturesMapper, "age", 32);

		// Then
		assertThat(newInstance).isEqualTo(createUnlabelledInstance("Patrick", "Star", 32));
	}

	@Test
	public void should_set_features_with_fixed_output_mapper() {
		// Given
		Instance instance = createUnlabelledInstance("Patrick");
		when(outputFixedFeaturesMapper.size()).thenReturn(3);
		when(outputFixedFeaturesMapper.getFeatureIndex("name")).thenReturn(1);
		when(outputFixedFeaturesMapper.getFeatureIndex("age")).thenReturn(2);

		Map<String, Object> features = new HashMap<>();
		features.put("age", 32);
		features.put("name", "Star");

		// When
		Instance newInstance = instance.withFeatures(outputFixedFeaturesMapper, features);

		// Then
		assertThat(newInstance).isEqualTo(createUnlabelledInstance("Patrick", "Star", 32));
	}

	@Test
	public void should_create_instance_with_fixed_output_mapper() {
		// Given
		when(outputFixedFeaturesMapper.size()).thenReturn(3);
		when(outputFixedFeaturesMapper.getFeatureIndex("firstname")).thenReturn(0);
		when(outputFixedFeaturesMapper.getFeatureIndex("name")).thenReturn(1);
		when(outputFixedFeaturesMapper.getFeatureIndex("age")).thenReturn(2);

		Map<String, Object> features = new HashMap<>();
		features.put("age", 32);
		features.put("firstname", "Patrick");
		features.put("name", "Star");

		// When
		Instance newInstance = Instance.newInstance(outputFixedFeaturesMapper, features);

		// Then
		assertThat(newInstance).isEqualTo(createUnlabelledInstance("Patrick", "Star", 32));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void should_create_instance_with_user_selection_output_mapper() {
		// Given
		when(outputUserSelectionFeaturesMapper.size()).thenReturn(3);

		List features = asList(32, "Patrick", "Star");

		// When
		Instance newInstance = Instance.newInstance(outputUserSelectionFeaturesMapper, features);

		// Then
		assertThat(newInstance).isEqualTo(createUnlabelledInstance(32, "Patrick", "Star"));
	}
	
	@Test
	public void should_add_features_with_user_selection_output_mapper() {
		// Given
		Instance instance = createUnlabelledInstance("Patrick", "Star");
		when(outputUserSelectionFeaturesMapper.size()).thenReturn(4);
		when(outputUserSelectionFeaturesMapper.getNewFeatureIndexes()).thenReturn(new int[] {2, 3});

		// When
		Instance newInstance = instance.withFeatures(outputUserSelectionFeaturesMapper, "Male", 32);

		// Then
		assertThat(newInstance).isEqualTo(createUnlabelledInstance("Patrick", "Star", "Male", 32));
	}
	
	@Test
	public void equals_and_hashcode_should_be_correclty_implemented() {
		assertThat(createUnlabelledInstance("Patrick", "Star", 32)).isEqualTo(createUnlabelledInstance("Patrick", "Star", 32));
		assertThat(createUnlabelledInstance("Patrick", "Star", 27)).isNotEqualTo(createUnlabelledInstance("Patrick", "Star", 32));
		assertThat(createUnlabelledInstance("Patrick", "Star", 32).hashCode()).isEqualTo(createUnlabelledInstance("Patrick", "Star", 32).hashCode());
		assertThat(createUnlabelledInstance("Patrick", "Star", 27).hashCode()).isNotEqualTo(createUnlabelledInstance("Patrick", "Star", 32).hashCode());
	}

	private Instance createUnlabelledInstance(Object... features) {
		return new Instance(null, features);

	}
}
