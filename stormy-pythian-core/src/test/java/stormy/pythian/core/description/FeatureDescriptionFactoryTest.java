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
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.NameMapper;

@RunWith(MockitoJUnitRunner.class)
public class FeatureDescriptionFactoryTest {

    @InjectMocks
    private FeatureDescriptionFactory factory;

    @Test
    public void should_create_descriptors() {
        // Given
        ExpectedFeature doubleFeature = mock(ExpectedFeature.class);
        when(doubleFeature.name()).thenReturn("double");
        when(doubleFeature.type()).thenReturn(Double.class);

        ExpectedFeature integerFeature = mock(ExpectedFeature.class);
        when(integerFeature.name()).thenReturn("integer");
        when(integerFeature.type()).thenReturn(Integer.class);

        NameMapper inputStream = mock(NameMapper.class);
        when(inputStream.expectedFeatures()).thenReturn(new ExpectedFeature[] { doubleFeature, integerFeature });

        // When
        List<FeatureDescription> descriptors = factory.createDescriptions(inputStream);

        // Then
        assertThat(descriptors).containsOnly(new FeatureDescription("double", Double.class), new FeatureDescription("integer", Integer.class));
    }

}
