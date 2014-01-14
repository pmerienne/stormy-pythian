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

import java.util.HashMap;
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
	private OutputFeaturesMapper outputFeaturesMapper;

	@Test
	public void should_get_feature_by_name() {
		// Given
		Instance instance = new Instance("Patrick", "Star", 32);

		String featureName = "age";
		when(inputFixedFeaturesMapper.getFeatureIndex("age")).thenReturn(2);

		// When
		Integer age = instance.getFeature(inputFixedFeaturesMapper, featureName);

		// Then
		assertThat(age).isEqualTo(32);
	}

	@Test
	public void should_get_null_feature_if_feature_does_not_exist() {
		// Given
		Instance instance = new Instance("Patrick", "Star", 32);

		String featureName = "age";
		when(inputFixedFeaturesMapper.getFeatureIndex("age")).thenReturn(-1);

		// When
		Integer age = instance.getFeature(inputFixedFeaturesMapper, featureName);

		// Then
		assertThat(age).isNull();
	}

	@Test
	public void should_get_selected_features() {
		// Given
		Instance instance = new Instance("Patrick", "Star", 32);
		when(inputUserSelectionFeaturesMapper.getSelectedIndex()).thenReturn(new int[] { 0, 2 });

		// When
		Object[] selectedFeatures = instance.getSelectedFeatures(inputUserSelectionFeaturesMapper);

		// Then
		assertThat(selectedFeatures).isEqualTo(new Object[]{"Patrick", 32});

	}

	@SuppressWarnings("serial")
	@Test
	public void should_apply_procedure() {
		// Given
		Instance instance = new Instance("Patrick", "Star", 32);
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
		Instance instance = new Instance("Patrick", "Star", 32);
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
		assertThat(newInstance).isEqualTo(new Instance("patrick", "star", 32));
	}

	@Test
	public void should_set_feature_with_input_mapper() {
		// Given
		Instance instance = new Instance("Patrick", "Star", 26);
		when(inputFixedFeaturesMapper.getFeatureIndex("age")).thenReturn(2);

		// When
		Instance newInstance = instance.withFeature(inputFixedFeaturesMapper, "age", 32);

		// Then
		assertThat(newInstance).isEqualTo(new Instance("Patrick", "Star", 32));
	}

	@Test
	public void should_set_features_with_input_mapper() {
		// Given
		Instance instance = new Instance("Patrick", "star", 27);
		when(inputFixedFeaturesMapper.getFeatureIndex("name")).thenReturn(1);
		when(inputFixedFeaturesMapper.getFeatureIndex("age")).thenReturn(2);

		Map<String, Object> features = new HashMap<>();
		features.put("age", 32);
		features.put("name", "Star");

		// When
		Instance newInstance = instance.withFeatures(inputFixedFeaturesMapper, features);

		// Then
		assertThat(newInstance).isEqualTo(new Instance("Patrick", "Star", 32));
	}

	@Test
	public void should_set_feature_with_output_mapper() {
		// Given
		Instance instance = new Instance("Patrick", "Star");
		when(outputFeaturesMapper.size()).thenReturn(3);
		when(outputFeaturesMapper.getFeatureIndex("age")).thenReturn(2);

		// When
		Instance newInstance = instance.withFeature(outputFeaturesMapper, "age", 32);

		// Then
		assertThat(newInstance).isEqualTo(new Instance("Patrick", "Star", 32));
	}

	@Test
	public void should_set_features_with_output_mapper() {
		// Given
		Instance instance = new Instance("Patrick");
		when(outputFeaturesMapper.size()).thenReturn(3);
		when(outputFeaturesMapper.getFeatureIndex("name")).thenReturn(1);
		when(outputFeaturesMapper.getFeatureIndex("age")).thenReturn(2);

		Map<String, Object> features = new HashMap<>();
		features.put("age", 32);
		features.put("name", "Star");

		// When
		Instance newInstance = instance.withFeatures(outputFeaturesMapper, features);

		// Then
		assertThat(newInstance).isEqualTo(new Instance("Patrick", "Star", 32));
	}

	@Test
	public void should_create_instance_with_output_mapper() {
		// Given
		when(outputFeaturesMapper.size()).thenReturn(3);
		when(outputFeaturesMapper.getFeatureIndex("firstname")).thenReturn(0);
		when(outputFeaturesMapper.getFeatureIndex("name")).thenReturn(1);
		when(outputFeaturesMapper.getFeatureIndex("age")).thenReturn(2);

		Map<String, Object> features = new HashMap<>();
		features.put("age", 32);
		features.put("firstname", "Patrick");
		features.put("name", "Star");
		
		// When
		Instance newInstance = Instance.newInstance(outputFeaturesMapper, features);

		// Then
		assertThat(newInstance).isEqualTo(new Instance("Patrick", "Star", 32));
	}
	
	@Test
	public void equals_and_hashcode_should_be_correclty_implemented() {
		assertThat(new Instance("Patrick", "Star", 32)).isEqualTo(new Instance("Patrick", "Star", 32));
		assertThat(new Instance("Patrick", "Star", 27)).isNotEqualTo(new Instance("Patrick", "Star", 32));
		assertThat(new Instance("Patrick", "Star", 32).hashCode()).isEqualTo(new Instance("Patrick", "Star", 32).hashCode());
		assertThat(new Instance("Patrick", "Star", 27).hashCode()).isNotEqualTo(new Instance("Patrick", "Star", 32).hashCode());
	}
}
