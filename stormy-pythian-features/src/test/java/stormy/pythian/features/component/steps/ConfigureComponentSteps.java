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
package stormy.pythian.features.component.steps;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.fest.assertions.Assertions.assertThat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import stormy.pythian.core.configuration.PropertyConfiguration;
import stormy.pythian.features.component.support.TestedComponent;
import stormy.pythian.model.component.PythianState;
import stormy.pythian.model.instance.Feature;
import stormy.pythian.model.instance.FeatureType;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.InstanceTestBuilder;
import stormy.pythian.model.instance.TextFeature;
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

    @Given("^the component has the input \"([^\"]*)\" mappings:$")
    public void the_component_has_the_input_mappings(String streamName, Map<String, String> mappings) throws Throwable {
        tested_component.set_input_mappings(streamName, mappings);
    }

    @Given("^the component has the output \"(.*?)\" mappings:$")
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
        assertThat(actualInstances).containsOnly(expectedInstances.toArray());
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

            Object value = PropertyTypeConverter.from(type).convert(type, rawValue);
            configurations.add(new PropertyConfiguration(name, value));
        }

        return configurations;
    }

    private List<Instance> toInstances(DataTable dataTable) {
        List<Instance> instances = new ArrayList<>();

        List<String> topCells = dataTable.topCells();
        Map<Integer, FeatureType> types = new HashMap<>();
        Map<Integer, String> names = new HashMap<>();

        int labelColumn = -1;
        int i = 0;
        for (String topCell : topCells) {
            String[] strings = topCell.split(":");
            types.put(i, FeatureType.from(strings[1]));
            if (equalsIgnoreCase("label", strings[0])) {
                labelColumn = i;
            } else {
                names.put(i, strings[0]);
            }
            i++;
        }

        List<List<String>> rows = dataTable.cells(1);
        for (List<String> row : rows) {
            InstanceTestBuilder builder = InstanceTestBuilder.instance();
            for (int column = 0; column < row.size(); column++) {
                String value = row.get(column);
                TextFeature rawFeature = new TextFeature(isNotBlank(value) ? value : null);
                Feature<?> feature = rawFeature.to(types.get(column));
                if (column == labelColumn) {
                    builder.label(feature);
                } else {
                    builder.with(names.get(column), feature);
                }
            }
            instances.add(builder.build());

        }
        return instances;
    }

}
