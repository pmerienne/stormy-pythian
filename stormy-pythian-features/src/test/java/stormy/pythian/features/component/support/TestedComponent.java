package stormy.pythian.features.component.support;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static stormy.pythian.model.instance.InputUserSelectionFeaturesMapperTestBuilder.inputUserSelectionFeaturesMapper;
import static stormy.pythian.model.instance.OutputFixedFeaturesMapperTestBuilder.outputFixedFeaturesMapper;
import static stormy.pythian.model.instance.OutputUserSelectionFeaturesMapperTestBuilder.outputUserSelectionFeaturesMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import stormy.pythian.core.configuration.PropertyConfiguration;
import stormy.pythian.core.utils.ReflectionHelper;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.component.PythianState;
import stormy.pythian.model.instance.InputUserSelectionFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFixedFeaturesMapper;
import stormy.pythian.model.instance.OutputUserSelectionFeaturesMapper;
import stormy.pythian.testing.InstanceCollector;
import stormy.pythian.testing.InstanceFeederSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.utils.Utils;
import cucumber.api.java.After;

public class TestedComponent {

    private static final int EMIT_WAIT_DURATION = 2000;
    private static final int DEPLOY_WAIT_DURATION = 2000;

    private final static Logger LOGGER = Logger.getLogger(TestedComponent.class);

    private final static LocalCluster CLUSTER;
    static {
        CLUSTER = new LocalCluster();
    }

    private Component component;

    private final TridentTopology trident_topology;
    private String topology_name;

    private final Config config;
    private final Map<String, InstanceCollector> output_collectors;
    private final Map<String, InstanceFeederSpout> feeders;

    public TestedComponent() {
        trident_topology = new TridentTopology();
        output_collectors = new HashMap<>();
        feeders = new HashMap<>();
        config = new Config();
        config.setMessageTimeoutSecs(1);
    }

    public void init_component(String componentClassName) throws Exception {
        component = (Component) Class.forName(componentClassName).newInstance();
        ReflectionHelper.setTopology(component, trident_topology);
        ReflectionHelper.setConfiguration(component, config);
    }

    public void set_properties(List<PropertyConfiguration> properties) {
        ReflectionHelper.setProperties(component, properties);
    }

    public void set_input_listed_features(String streamName, List<String> selectedFeature) {
        InputUserSelectionFeaturesMapper mapper = inputUserSelectionFeaturesMapper(selectedFeature).select(selectedFeature).build();
        ReflectionHelper.setFeaturesMapper(component, streamName, mapper);
    }

    public void set_output_listed_feature(String outputStreamName, List<String> features) {
        OutputUserSelectionFeaturesMapper outputMapper = outputUserSelectionFeaturesMapper(features).select(features).build();
        ReflectionHelper.setFeaturesMapper(component, outputStreamName, outputMapper);
    }

    public void set_output_mapped_feature(String outputStreamName, Map<String, String> mappings) {
        String[] features = new ArrayList<>(mappings.values()).toArray(new String[0]);
        OutputFixedFeaturesMapper outputMapper = outputFixedFeaturesMapper(features).map(mappings).build();
        ReflectionHelper.setFeaturesMapper(component, outputStreamName, outputMapper);
    }

    public void set_state(String state_name, Class<PythianState> state_clazz, List<PropertyConfiguration> properties) {
        try {
            PythianState state = state_clazz.newInstance();
            ReflectionHelper.setProperties(state, properties);
            ReflectionHelper.setStateFactory(component, state_name, state.createStateFactory());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to instanciate " + state_clazz, e);
        }
    }

    public void deploy() {
        List<String> inputStreamNames = ReflectionHelper.getInputStreamNames(component.getClass());
        for (String streamName : inputStreamNames) {
            InstanceFeederSpout feeder = new InstanceFeederSpout();
            feeders.put(streamName, feeder);

            Stream feederStream = trident_topology.newStream(streamName + "-feeder", feeder);
            ReflectionHelper.setInputStream(component, streamName, feederStream);
        }

        component.init();

        Map<String, Stream> outputStreams = ReflectionHelper.getOutputStreams(component);
        for (String output : outputStreams.keySet()) {
            InstanceCollector collector = new InstanceCollector();
            collector.collect(outputStreams.get(output));
            output_collectors.put(output, collector);
        }

        topology_name = generate_topology_name();
        CLUSTER.submitTopology(topology_name, config, trident_topology.build());

        Utils.sleep(DEPLOY_WAIT_DURATION);
    }

    public void emit(String stream_name, List<Instance> instances) {
        if (!feeders.containsKey(stream_name)) {
            throw new IllegalArgumentException("Component has no input named \"" + stream_name + "\"");
        }
        feeders.get(stream_name).feedWith(instances);
        Utils.sleep(EMIT_WAIT_DURATION);
    }

    public List<Instance> retrieve_emited_instance(String output) {
        if (!output_collectors.containsKey(output)) {
            throw new IllegalArgumentException("Component has no output named \"" + output + "\"");
        }
        return output_collectors.get(output).getCollected();
    }

    @After
    public void killTopology() {
        if (topology_name != null) {
            try {
                CLUSTER.killTopology(topology_name);
            } catch (Exception ex) {
                LOGGER.warn("Error while killing topology" + topology_name, ex);
            }
        }
    }

    private String generate_topology_name() {
        return component.getClass().getSimpleName() + "-test-" + randomAlphanumeric(5);
    }

}
