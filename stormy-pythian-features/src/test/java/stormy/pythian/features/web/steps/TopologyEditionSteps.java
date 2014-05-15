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
package stormy.pythian.features.web.steps;

import static org.fest.assertions.Assertions.assertThat;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import stormy.pythian.features.web.support.Component;
import stormy.pythian.features.web.support.Components;
import stormy.pythian.features.web.support.Property;
import stormy.pythian.features.web.support.States;
import stormy.pythian.features.web.support.Topologies;
import stormy.pythian.features.web.support.WebConnector;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class TopologyEditionSteps {

    private final WebConnector connector;
    private final Topologies topologies;
    private final Components components;
    private final States states;

    public TopologyEditionSteps(WebConnector connector, Topologies topologies, Components components, States states) {
        this.connector = connector;
        this.topologies = topologies;
        this.components = components;
        this.states = states;
    }

    @Given("^a topology named \"([^\"]*)\" with the components:$")
    public void a_topology_named_with_the_components(String topologyName, DataTable dataTable) throws Throwable {
        List<Component> components = dataTable.asList(Component.class);
        topologies.create_new_topology(topologyName, components);
    }

    @When("^I add a new \"([^\"]*)\" \"([^\"]*)\" component named \"([^\"]*)\"$")
    public void i_add_a_new_component(String type, String component, String name) {
        components.add_new_component(type, component, name, 50, 50);
    }

    @Then("^I should see a component named \"([^\"]*)\"$")
    public void i_should_see_a_component_named(String componentName) throws Throwable {
        By component_title = by_component_title(componentName);
        assertThat(connector.element_exists(component_title)).isTrue();
    }

    private By by_component_title(String componentName) {
        return By.xpath("//*[contains(@class,'diagram-component-title') and contains(text(),'" + componentName + "')]");
    }

    @When("^I connect \"([^\"]*)\" \"([^\"]*)\" to \"([^\"]*)\" \"([^\"]*)\"$")
    public void i_connect(String component1Name, String component1Stream, String component2Name, String component2Stream) throws Throwable {
        topologies.connect(component1Name, component1Stream, component2Name, component2Stream);
    }

    @Given("^\"(.*?)\" \"(.*?)\" is connected to \"(.*?)\" \"(.*?)\"$")
    public void is_connected_to(String component1Name, String component1Stream, String component2Name, String component2Stream) throws Throwable {
        topologies.connect(component1Name, component1Stream, component2Name, component2Stream);
    }

    @Then("^I should see a link between \"([^\"]*)\" \"([^\"]*)\" and \"([^\"]*)\" \"([^\"]*)\"$")
    public void i_should_see_a_link_between(String component1Name, String component1Stream, String component2Name, String component2Stream) throws Throwable {
        String sourceComponent = components.getId(component1Name);
        String targetComponent = components.getId(component2Name);

        Boolean hasConnections = (Boolean) connector.executeScript("return jsPlumb.getConnections({source:arguments[0], target:arguments[1]}).length > 0;",
                sourceComponent,
                targetComponent);
        assertThat(hasConnections).isTrue();
    }

    @When("^I set the \"([^\"]*)\" component properties:$")
    public void i_set_the_properties(String componentName, DataTable datatable) throws Throwable {
        List<Property> properties = datatable.asList(Property.class);
        components.set_properties(componentName, properties);
    }

    @Then("^the component \"([^\"]*)\" should have the following properties:$")
    public void the_component_should_have_the_following_properties(String componentName, DataTable datatable) throws Throwable {
        connector.click(By.xpath("//*[contains(@class,'diagram-component-title') and contains(text(),'" + componentName + "')]"));
        connector.click("properties-tab-heading");

        List<Property> properties = datatable.asList(Property.class);
        for (Property property : properties) {
            String actualValue = components.get_property_value(componentName, property.type, property.name);
            assertThat(actualValue).isEqualTo(property.value);
        }

        connector.press(Keys.ESCAPE);
    }

    @When("^I go to the \"([^\"]*)\" state view of \"([^\"]*)\"")
    public void i_go_to_the_states_view(String stateName, String componentName) throws Throwable {
        states.go_to_state_view(componentName, stateName);
    }

    @Then("^I should see a list of available states$")
    public void i_should_see_a_list_of_available_states() throws Throwable {
        WebElement element = connector.retrieve_element(By.id("state-description"));
        Select select_element = new Select(element);
        assertThat(select_element.getOptions()).isNotEmpty();
    }

    @When("^I choose \"([^\"]*)\" for the \"([^\"]*)\" state of \"([^\"]*)\"")
    public void i_choose_the_state_description(String descriptionName, String stateName, String componentName) throws Throwable {
        states.go_to_state_view(componentName, stateName);
        states.set_state_description(descriptionName);
        states.save_and_close();

    }

    @When("^I set the \"([^\"]*)\" state of \"([^\"]*)\" properties:$")
    public void I_set_the_state_of_properties(String stateName, String componentName, DataTable datatable) throws Throwable {
        List<Property> asList = datatable.asList(Property.class);

        states.go_to_state_view(componentName, stateName);
        states.set_properties(asList);
        states.save_and_close();
    }

    @Then("^the \"([^\"]*)\" state of \"([^\"]*)\" should have the following properties:$")
    public void the_state_of_should_have_the_following_properties(String stateName, String componentName, DataTable datatable) throws Throwable {
        List<Property> properties = datatable.asList(Property.class);

        states.go_to_state_view(componentName, stateName);
        for (Property property : properties) {
            String actualValue = states.get_property_value(property.type, property.name);
            assertThat(actualValue).isEqualTo(property.value);
        }
        states.close();
    }

    @When("^I add the features (.+) to the listed output mapping \"(.*?)\" of \"(.*?)\"$")
    public void i_add_the_features_to_the_listed_output_mapping_of(List<String> new_features, String stream_name, String component_name) throws Throwable {
        components.set_output_mappings(component_name, stream_name, new_features);
    }

    @Then("^I should be proposed (.+) in the listed input mapping \"(.*?)\" of \"(.*?)\"$")
    public void i_should_be_proposed_in_the_listed_output_mapping_of(List<String> features, String stream_name, String component_name) throws Throwable {
        List<String> available_features = components.get_available_listed_input_features(component_name, stream_name);
        assertThat(available_features).isEqualTo(features);
    }

    @When("^I set the \"(.*?)\" of the output mapping \"(.*?)\" of component \"(.*?)\" to \"(.*?)\"$")
    public void i_set_the_of_the_output_mapping_of_component_to(String component_feature_name, String stream_name, String component_name, String topology_feature_name) throws Throwable {
        components.set_output_mapping(component_name, stream_name, component_feature_name, topology_feature_name);
    }

}
