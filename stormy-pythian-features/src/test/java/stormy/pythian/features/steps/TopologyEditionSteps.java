package stormy.pythian.features.steps;

import static org.fest.assertions.Assertions.assertThat;
import java.util.List;
import org.openqa.selenium.By;
import stormy.pythian.features.support.Component;
import stormy.pythian.features.support.Components;
import stormy.pythian.features.support.Topologies;
import stormy.pythian.features.support.WebConnector;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class TopologyEditionSteps {

    private final WebConnector connector;
    private final Topologies topologies;
    private final Components components;

    public TopologyEditionSteps(WebConnector connector, Topologies topologies, Components components) {
        this.connector = connector;
        this.topologies = topologies;
        this.components = components;
    }

    @Given("^a topology named \"([^\"]*)\" with the components:$")
    public void a_topology_named_with_the_components(String topologyName, DataTable dataTable) throws Throwable {
        List<Component> components = dataTable.asList(Component.class);
        topologies.create_new_topology(topologyName, components);
    }

    @When("^I add a new \"([^\"]*)\" component : \"([^\"]*)\"$")
    public void i_add_a_new_component(String type, String name) {
        topologies.add_new_component(type, name, 50, 50);
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

    @Then("^I should see a link between \"([^\"]*)\" \"([^\"]*)\" and \"([^\"]*)\" \"([^\"]*)\"$")
    public void i_should_see_a_link_between(String component1Name, String component1Stream, String component2Name, String component2Stream) throws Throwable {
        String sourceComponent = components.getId(component1Name);
        String targetComponent = components.getId(component2Name);

        Boolean hasConnections = (Boolean) connector.executeScript("return jsPlumb.getConnections({source:arguments[0], target:arguments[1]}).length > 0;",
                sourceComponent,
                targetComponent);
        assertThat(hasConnections).isTrue();
    }

}
