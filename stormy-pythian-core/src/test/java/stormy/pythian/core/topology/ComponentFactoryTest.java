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
package stormy.pythian.core.topology;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_MAP;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static stormy.pythian.model.annotation.MappingType.LISTED;
import static stormy.pythian.model.annotation.MappingType.NAMED;
import static stormy.pythian.model.instance.FeatureType.ANY;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.state.StateFactory;
import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.core.configuration.InputStreamConfiguration;
import stormy.pythian.core.configuration.OutputStreamConfiguration;
import stormy.pythian.core.configuration.PropertyConfiguration;
import stormy.pythian.core.description.ComponentDescription;
import stormy.pythian.core.description.FeatureDescription;
import stormy.pythian.core.description.InputStreamDescription;
import stormy.pythian.core.description.OutputStreamDescription;
import stormy.pythian.model.annotation.Configuration;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.ListMapper;
import stormy.pythian.model.annotation.NameMapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.annotation.State;
import stormy.pythian.model.annotation.Topology;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.ListedFeaturesMapper;
import stormy.pythian.model.instance.NamedFeaturesMapper;
import backtype.storm.Config;

@SuppressWarnings("serial")
@RunWith(MockitoJUnitRunner.class)
public class ComponentFactoryTest {

    @InjectMocks
    private ComponentFactory factory;

    @Mock
    private TridentTopology tridentTopology;

    @Mock
    private Config config;

    @Test
    public void should_create_and_init_component() {
        // Given
        ComponentConfiguration configuration = new ComponentConfiguration(randomAlphabetic(6), new ComponentDescription(TestComponent.class));

        // When
        Component component = factory.createComponent(configuration, new HashMap<String, StateFactory>(), new HashMap<String, Stream>());

        // Then
        assertThat(component).isInstanceOf(TestComponent.class);

        TestComponent testComponent = (TestComponent) component;
        assertThat(testComponent.isReady).isTrue();
    }

    @Test
    public void should_set_properties() {
        // Given
        ComponentConfiguration configuration = new ComponentConfiguration(randomAlphabetic(6), new ComponentDescription(TestComponent.class));
        configuration.add(new PropertyConfiguration("distributed", true));

        // When
        Component component = factory.createComponent(configuration, new HashMap<String, StateFactory>(), new HashMap<String, Stream>());

        // Then
        TestComponent testComponent = (TestComponent) component;
        assertThat(testComponent.isDistributed).isTrue();
    }

    @Test
    public void should_set_input_streams() {
        // Given
        ComponentConfiguration configuration = new ComponentConfiguration(randomAlphabetic(6), new ComponentDescription(TestComponent.class));

        Map<String, Stream> inputStreams = new HashMap<String, Stream>();
        Stream originalInputStream = mock(Stream.class);
        inputStreams.put("in1", originalInputStream);

        Stream switchedStream = mock(Stream.class);
        when(originalInputStream.applyAssembly(isA(ReplaceInstanceField.class))).thenReturn(switchedStream);

        // When
        Component component = factory.createComponent(configuration, new HashMap<String, StateFactory>(), inputStreams);

        // Then
        assertThat(component).isInstanceOf(TestComponent.class);

        TestComponent testComponent = (TestComponent) component;
        assertThat(testComponent.in1).isEqualTo(switchedStream);
    }

    @Test
    public void should_set_topology() {
        // Given
        ComponentConfiguration configuration = new ComponentConfiguration(randomAlphabetic(6), new ComponentDescription(TestComponent.class));

        Map<String, Stream> inputStreams = new HashMap<String, Stream>();
        Stream expectedStream = mock(Stream.class);
        inputStreams.put("in1", expectedStream);

        // When
        TestComponent component = (TestComponent) factory.createComponent(configuration, new HashMap<String, StateFactory>(), inputStreams);

        // Then
        assertThat(component.topology).isEqualTo(tridentTopology);
    }

    @Test
    public void should_set_configuration() {
        // Given
        ComponentConfiguration configuration = new ComponentConfiguration(randomAlphabetic(6), new ComponentDescription(TestComponent.class));

        Map<String, Stream> inputStreams = new HashMap<String, Stream>();
        Stream expectedStream = mock(Stream.class);
        inputStreams.put("in1", expectedStream);

        // When
        TestComponent component = (TestComponent) factory.createComponent(configuration, new HashMap<String, StateFactory>(), inputStreams);

        // Then
        assertThat(component.configuration).isEqualTo(config);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_set_intput_stream_mapper() {
        // Given
        InputStreamDescription inputStreamDescription = new InputStreamDescription("in1", LISTED);
        List<String> selectedFeatures = Arrays.asList("age", "viewCount");
        InputStreamConfiguration inputStreamConfiguration = new InputStreamConfiguration(inputStreamDescription, selectedFeatures);

        ComponentConfiguration configuration = new ComponentConfiguration(randomAlphabetic(6), new ComponentDescription(TestComponent.class));
        configuration.add(inputStreamConfiguration);

        // When
        TestComponent component = (TestComponent) factory.createComponent(configuration, new HashMap<String, StateFactory>(), EMPTY_MAP);

        // Then
        assertThat(component.in1Mapper).isEqualTo(new ListedFeaturesMapper(selectedFeatures));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_set_fixed_feature_output_stream_mapper() {
        // Given
        Map<String, String> mappings = new HashMap<>();
        mappings.put("inside", "outside");
        OutputStreamDescription outputStreamDescription = new OutputStreamDescription("out1", "", NAMED, asList(new FeatureDescription("outside", ANY)));

        ComponentConfiguration configuration = new ComponentConfiguration(randomAlphabetic(6), new ComponentDescription(TestComponent.class));
        configuration.add(new OutputStreamConfiguration(outputStreamDescription, mappings));

        // When
        TestComponent component = (TestComponent) factory.createComponent(configuration, new HashMap<String, StateFactory>(), EMPTY_MAP);

        // Then
        assertThat(component.out1Mapper).isEqualTo(new NamedFeaturesMapper(mappings));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_set_user_selection_output_stream_mapper() {
        // Given
        List<String> selectedFeatures = asList("path", "response time", "ip");
        OutputStreamDescription outputStreamDescription = new OutputStreamDescription("out2", "", LISTED, new ArrayList<FeatureDescription>());

        ComponentConfiguration configuration = new ComponentConfiguration(randomAlphabetic(6), new ComponentDescription(TestComponent.class));
        configuration.add(new OutputStreamConfiguration(outputStreamDescription, selectedFeatures));

        // When
        TestComponent component = (TestComponent) factory.createComponent(configuration, new HashMap<String, StateFactory>(), EMPTY_MAP);

        // Then
        assertThat(component.out2Mapper).isEqualTo(new ListedFeaturesMapper(selectedFeatures));
    }

    @Test
    public void should_set_state_factories() {
        // Given
        StateFactory expectedStateFactory = mock(StateFactory.class);
        Map<String, StateFactory> topologyStateFactories = new HashMap<>();
        topologyStateFactories.put("Word count", expectedStateFactory);

        ComponentConfiguration configuration = new ComponentConfiguration(randomAlphabetic(6), new ComponentDescription(TestComponent.class));

        // When
        Component component = factory.createComponent(configuration, topologyStateFactories, new HashMap<String, Stream>());

        // Then
        TestComponent testComponent = (TestComponent) component;
        assertThat(testComponent.stateFactory).isEqualTo(expectedStateFactory);
    }

    public static class TestComponent implements Component {

        @Topology
        public TridentTopology topology;

        @Configuration
        public Config configuration;

        @Property(name = "distributed")
        public Boolean isDistributed;

        @InputStream(name = "in1")
        public Stream in1;

        @OutputStream(from = "in1", name = "out1")
        public Stream outputStream;

        @ListMapper(stream = "in1")
        public ListedFeaturesMapper in1Mapper;

        @NameMapper(stream = "out1", expectedFeatures = {})
        public NamedFeaturesMapper out1Mapper;

        @ListMapper(stream = "out2")
        public ListedFeaturesMapper out2Mapper;

        @State(name = "Word count")
        private StateFactory stateFactory;

        public boolean isReady = false;

        @Override
        public void init() {
            isReady = true;
        }
    }
}
