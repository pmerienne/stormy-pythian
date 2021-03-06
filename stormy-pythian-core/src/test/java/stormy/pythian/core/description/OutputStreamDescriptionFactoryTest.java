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
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static stormy.pythian.model.annotation.MappingType.LISTED;
import static stormy.pythian.model.annotation.MappingType.NAMED;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import storm.trident.Stream;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.ListMapper;
import stormy.pythian.model.annotation.NameMapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.ListedFeaturesMapper;
import stormy.pythian.model.instance.NamedFeaturesMapper;

@SuppressWarnings("serial")
@RunWith(MockitoJUnitRunner.class)
public class OutputStreamDescriptionFactoryTest {

    @InjectMocks
    private OutputStreamDescriptionFactory factory;

    @Mock
    private FeatureDescriptionFactory featureDescriptorFactory;

    @Test
    public void should_retrieve_output_stream_descriptions() {
        // Given
        @Documentation(name = "Test component")
        class TestComponent implements Component {

            @InputStream(name = "in")
            public Stream in;

            @ListMapper(stream = "in")
            public ListedFeaturesMapper inMapper;

            @OutputStream(from = "in", name = "out1")
            private Stream out1;

            @NameMapper(stream = "out1", expectedFeatures = {})
            public NamedFeaturesMapper out1Mapper;

            @OutputStream(from = "", name = "out2")
            private Stream out2;

            @NameMapper(stream = "out2", expectedFeatures = {})
            public NamedFeaturesMapper out2Mapper;

            @Override
            public void init() {
            }
        }

        List<FeatureDescription> newFeatures = new ArrayList<>();
        when(featureDescriptorFactory.createDescriptions(isA(NameMapper.class))).thenReturn(newFeatures);

        // When
        List<OutputStreamDescription> actualDescriptions = factory.createOutputStreamDescriptions(TestComponent.class);

        // Then
        assertThat(actualDescriptions).hasSize(2);
        assertThat(actualDescriptions).containsOnly( //
                new OutputStreamDescription("out1", "in", NAMED, newFeatures), //
                new OutputStreamDescription("out2", "", NAMED, newFeatures) //
                );
    }

    @Test
    public void should_retrieve_user_selection_output_stream_descriptions() {
        // Given
        @Documentation(name = "Test component")
        class TestComponent implements Component {

            @OutputStream(from = "in", name = "out1")
            private Stream out1;

            @ListMapper(stream = "out1")
            public ListedFeaturesMapper out1Mapper;

            @OutputStream(name = "out2")
            private Stream out2;

            @ListMapper(stream = "out2")
            public ListedFeaturesMapper out2Mapper;

            @Override
            public void init() {
            }
        }

        // When
        List<OutputStreamDescription> actualDescriptions = factory.createOutputStreamDescriptions(TestComponent.class);

        // Then
        assertThat(actualDescriptions).containsOnly( // 
                new OutputStreamDescription("out1", "in", LISTED, new ArrayList<FeatureDescription>()), //
                new OutputStreamDescription("out2", "", LISTED, new ArrayList<FeatureDescription>()) //
                );
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_illegal_argument_exception_when_output_stream_annotation_not_applied_on_stream() {
        // Given
        @Documentation(name = "Test component")
        class TestComponent implements Component {

            @InputStream(name = "in")
            public Stream in;

            @OutputStream(from = "in", name = "out1")
            private Object out1;

            @NameMapper(stream = "out1", expectedFeatures = {})
            public NamedFeaturesMapper out1Mapper;

            @Override
            public void init() {
            }
        }

        // When
        factory.createOutputStreamDescriptions(TestComponent.class);
    }

}
