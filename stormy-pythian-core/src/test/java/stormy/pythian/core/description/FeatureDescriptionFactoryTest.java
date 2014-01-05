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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static stormy.pythian.model.instance.FeatureType.FLOAT;
import static stormy.pythian.model.instance.FeatureType.INTEGER;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.OutputStream;

@RunWith(MockitoJUnitRunner.class)
public class FeatureDescriptionFactoryTest {

	@InjectMocks
	private FeatureDescriptionFactory factory;

	@Test
	public void should_create_descriptor() {
		// Given
		ExpectedFeature floatFeature = mock(ExpectedFeature.class);
		when(floatFeature.name()).thenReturn("float");
		when(floatFeature.type()).thenReturn(FLOAT);

		// When
		FeatureDescription descriptor = factory.createDescription(floatFeature);

		// Then
		assertThat(descriptor).isEqualTo(new FeatureDescription("float", FLOAT));
	}

	@Test
	public void should_create_descriptors_from_inputstream() {
		// Given
		ExpectedFeature floatFeature = mock(ExpectedFeature.class);
		when(floatFeature.name()).thenReturn("float");
		when(floatFeature.type()).thenReturn(FLOAT);

		ExpectedFeature integerFeature = mock(ExpectedFeature.class);
		when(integerFeature.name()).thenReturn("integer");
		when(integerFeature.type()).thenReturn(INTEGER);

		InputStream inputStream = mock(InputStream.class);
		when(inputStream.expectedFeatures()).thenReturn(new ExpectedFeature[] { floatFeature, integerFeature });

		// When
		List<FeatureDescription> descriptors = factory.createDescriptions(inputStream);

		// Then
		assertThat(descriptors).containsOnly(new FeatureDescription("float", FLOAT), new FeatureDescription("integer", INTEGER));
	}

	@Test
	public void should_create_descriptors_from_outputstream() {
		// Given
		ExpectedFeature floatFeature = mock(ExpectedFeature.class);
		when(floatFeature.name()).thenReturn("float");
		when(floatFeature.type()).thenReturn(FLOAT);

		ExpectedFeature integerFeature = mock(ExpectedFeature.class);
		when(integerFeature.name()).thenReturn("integer");
		when(integerFeature.type()).thenReturn(INTEGER);

		OutputStream outputStream = mock(OutputStream.class);
		when(outputStream.newFeatures()).thenReturn(new ExpectedFeature[] { floatFeature, integerFeature });

		// When
		List<FeatureDescription> descriptors = factory.createDescriptions(outputStream);

		// Then
		assertThat(descriptors).containsOnly(new FeatureDescription("float", FLOAT), new FeatureDescription("integer", INTEGER));
	}
}
