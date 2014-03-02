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
package stormy.pythian.core.description;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static stormy.pythian.model.annotation.MappingType.FIXED_FEATURES;
import static stormy.pythian.model.annotation.MappingType.USER_SELECTION;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import storm.trident.Stream;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.component.Component;

@SuppressWarnings("serial")
@RunWith(MockitoJUnitRunner.class)
public class InputStreamDescriptionFactoryTest {

	@InjectMocks
	private InputStreamDescriptionFactory factory;

	@Mock
	private FeatureDescriptionFactory featureDescriptorFactory;

	@Test
	public void should_retrieve_input_stream_descriptions() {
		// Given
		@Documentation(name = "Test component")
		class TestComponent implements Component {

			@InputStream(name = "in1", type = USER_SELECTION)
			private Stream in1;

			@InputStream(name = "in2", type = FIXED_FEATURES, expectedFeatures = {})
			private Stream in2;

			@Override
			public void init() {
			}
		}

		List<FeatureDescription> expectedFeatures = new ArrayList<>();

		when(featureDescriptorFactory.createDescriptions(Mockito.isA(InputStream.class))) //
				.thenReturn(expectedFeatures);

		// When
		List<InputStreamDescription> actualDescriptions = factory.createInputStreamDescriptions(TestComponent.class);

		// Then
		assertThat(actualDescriptions).containsOnly( //
				new InputStreamDescription("in1", USER_SELECTION), //
				new InputStreamDescription("in2", FIXED_FEATURES, expectedFeatures) //
				);
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_illegal_argument_exception_when_input_stream_annotation_not_applied_on_stream() {
		// Given
		@Documentation(name = "Test component")
		class TestComponent implements Component {

			@InputStream(name = "in", type = USER_SELECTION)
			private Object in;

			@Override
			public void init() {
			}
		}

		// When
		factory.createInputStreamDescriptions(TestComponent.class);
	}

	@Test
	public void should_retrieve_inherited_input_stream_descriptions() {
		// Given
		@Documentation(name = "Test component")
		class AbstractTestComponent implements Component {

			@InputStream(name = "in1", type = USER_SELECTION)
			private Stream in1;

			@InputStream(name = "in2", type = FIXED_FEATURES, expectedFeatures = {})
			private Stream in2;

			@Override
			public void init() {
			}
		}
		
		class TestComponent extends AbstractTestComponent {
			
		}

		List<FeatureDescription> expectedFeatures = new ArrayList<>();

		when(featureDescriptorFactory.createDescriptions(Mockito.isA(InputStream.class))) //
				.thenReturn(expectedFeatures);

		// When
		List<InputStreamDescription> actualDescriptions = factory.createInputStreamDescriptions(TestComponent.class);

		// Then
		assertThat(actualDescriptions).containsOnly( //
				new InputStreamDescription("in1", USER_SELECTION), //
				new InputStreamDescription("in2", FIXED_FEATURES, expectedFeatures) //
				);
	}
}
