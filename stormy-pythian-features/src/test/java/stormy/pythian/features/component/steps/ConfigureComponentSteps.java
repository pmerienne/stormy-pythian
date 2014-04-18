package stormy.pythian.features.component.steps;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.fest.assertions.Assertions.assertThat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import stormy.pythian.core.configuration.PropertyConfiguration;
import stormy.pythian.features.component.support.TestedComponent;
import stormy.pythian.model.component.PythianState;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.InstanceTestBuilder;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ConfigureComponentSteps {

    private TestedComponent tested_component;

    public ConfigureComponentSteps(TestedComponent testedComponent) {
        this.tested_component = testedComponent;
    }

    @Given("^a component \"([^\"]*)\" configured with:$")
    public void a_component_is_configured_with(String componentClassName, DataTable properties) throws Throwable {
        List<PropertyConfiguration> propertyconfigurations = toPropertyConfiguration(properties);

        tested_component.init_component(componentClassName);
        tested_component.set_properties(propertyconfigurations);
    }

    @Given("^a component \"(.*?)\"$")
    public void a_component(String componentClassName) throws Throwable {
        tested_component.init_component(componentClassName);
    }

    @Given("^the component has the output \"([^\"]*)\" listed features: ((?:\\s|\\w|,)+)$")
    public void the_component_has_the_output_listed_features(String outputStreamName, List<String> selectedFeature) throws Throwable {
        tested_component.set_output_listed_feature(outputStreamName, selectedFeature);
    }

    @Given("^the component has the input \"([^\"]*)\" listed features: ((?:\\s|\\w|,)+)$")
    public void the_component_has_the_input_listed_features_i_i_i(String streamName, List<String> selectedFeature) throws Throwable {
        tested_component.set_input_listed_features(streamName, selectedFeature);
    }

    @Given("^the component has the output \"(.*?)\" named mappings:$")
    public void the_component_has_the_output_named_features(String outputStreamName, Map<String, String> mappings) throws Throwable {
        tested_component.set_output_mapped_feature(outputStreamName, mappings);
    }

    @Given("^the component has the output \"(.*?)\" named mapper empty$")
    public void the_component_has_the_output_named_mapper_empty(String outputStreamName) throws Throwable {
        tested_component.set_output_mapped_feature(outputStreamName, new HashMap<String, String>());
    }

    @SuppressWarnings("unchecked")
    @Given("^the component has the state \"(.*?)\" configured with \"(.*?)\":$")
    public void the_component_has_the_state_configured_with(String state_name, String state_class, DataTable properties) throws Throwable {
        List<PropertyConfiguration> propertyconfigurations = toPropertyConfiguration(properties);
        Class<PythianState> state_clazz = (Class<PythianState>) Class.forName(state_class);
        tested_component.set_state(state_name, state_clazz, propertyconfigurations);
    }

    @When("^the component is deployed$")
    public void the_component_is_deployed() throws Throwable {
        tested_component.deploy();
    }

    @Then("^the component's output \"([^\"]*)\" should have emit only:$")
    public void the_component_output_should_have_emit_only(String outputStreamName, DataTable expectedInstancesDataTable) throws Throwable {
        this.the_output_should_have_emit_only(outputStreamName, expectedInstancesDataTable);
    }

    @Then("^the output \"(.*?)\" should have emit only:$")
    public void the_output_should_have_emit_only(String outputStreamName, DataTable expectedInstancesDataTable) throws Throwable {
        List<Instance> expectedInstances = toInstances(expectedInstancesDataTable);
        List<Instance> actualInstances = tested_component.retrieve_emited_instance(outputStreamName);
        assertThat(actualInstances).containsExactly(expectedInstances.toArray());
    }

    @When("^I emit to the input \"(.*?)\":$")
    public void i_emit_to_the_input(String streamName, DataTable instancesDataTable) throws Throwable {
        List<Instance> instances = toInstances(instancesDataTable);
        tested_component.emit(streamName, instances);
    }

    private List<PropertyConfiguration> toPropertyConfiguration(DataTable properties) {
        List<PropertyConfiguration> configurations = new ArrayList<>();

        List<List<String>> rawLines = properties.cells(1);
        for (List<String> rawLine : rawLines) {
            String name = rawLine.get(0);
            String type = rawLine.get(1);
            String rawValue = rawLine.get(2);

            Object value = TypeConverter.from(type).convert(type, rawValue);
            configurations.add(new PropertyConfiguration(name, value));
        }

        return configurations;
    }

    private List<Instance> toInstances(DataTable dataTable) {
        List<Instance> instances = new ArrayList<>();

        int labelColumn = -1;
        List<String> topCells = dataTable.topCells();
        Map<Integer, TypeConverter> converters = new HashMap<>();
        int i = 0;
        for (String topCell : topCells) {
            String[] strings = topCell.split(":");
            converters.put(i, TypeConverter.from(strings[1]));
            if (equalsIgnoreCase("label", strings[0])) {
                labelColumn = i;
            }
            i++;
        }

        List<List<String>> rows = dataTable.cells(1);
        for (List<String> row : rows) {
            InstanceTestBuilder builder = InstanceTestBuilder.instance();
            for (int column = 0; column < row.size(); column++) {
                Object feature = converters.get(column).convert(row.get(column));
                if (column == labelColumn) {
                    builder.label(feature);
                } else {
                    builder.with(feature);
                }
            }
            instances.add(builder.build());

        }
        return instances;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static enum TypeConverter {
        STRING {
            @Override
            public Object convert(String type, String value) {
                return value;
            }
        },
        DOUBLE {
            @Override
            public Object convert(String type, String value) {
                return Double.valueOf(value);
            }
        },
        INTEGER {
            @Override
            public Object convert(String type, String value) {
                return Integer.valueOf(value);
            }
        },
        BOOLEAN {
            @Override
            public Object convert(String type, String value) {
                return Boolean.valueOf(value);
            }
        },
        ENUM {
            @Override
            public Object convert(String type, String value) {
                String enumName = type.split(":")[1];
                try {
                    Class enumType = Class.forName(enumName);
                    return Enum.valueOf(enumType, value);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("No enum found for name " + enumName);
                }
            }
        };

        public abstract Object convert(String type, String value);

        public Object convert(String value) {
            return convert(null, value);
        }

        public static TypeConverter from(String type) {
            if (StringUtils.startsWithIgnoreCase(type, ENUM.name() + ":")) {
                return ENUM;
            }

            for (TypeConverter converter : TypeConverter.values()) {
                if (StringUtils.equalsIgnoreCase(converter.name(), type)) {
                    return converter;
                }
            }
            throw new IllegalArgumentException("No type converter found for " + type);
        }
    }
}
