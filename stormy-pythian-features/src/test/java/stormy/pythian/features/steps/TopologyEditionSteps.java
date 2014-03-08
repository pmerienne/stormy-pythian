package stormy.pythian.features.steps;

import static org.fest.assertions.Assertions.assertThat;
import static org.openqa.selenium.By.linkText;
import static stormy.pythian.features.support.Environment.BASE_HTML_PATH;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
        create_new_topology(topologyName, components);
    }

    @When("^I add a new \"([^\"]*)\" component : \"([^\"]*)\"$")
    public void i_add_a_new_component(String type, String name) {
        add_new_component(type, name, 50, 50);
        connector.click("save-topology");
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
        WebElement outputEndpoint = retrieve_output_endpoint(component1Name, component1Stream);
        WebElement inputEndpoint = retrieve_input_endpoint(component2Name, component2Stream);
        connector.drag_and_drop(outputEndpoint, inputEndpoint);
        connector.click("save-topology");
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

    private String retrieve_component_id(String componentName) {
        WebElement component = connector.retrieve_element(By.xpath("//*[contains(@class,'diagram-component-title') and contains(text(),'" + componentName + "')]/.."));
        String id = component.getAttribute("id");
        return id;
    }

    private WebElement retrieve_component(String name) {
        String componentId = components.getId(name);
        return connector.retrieve_element(By.id(componentId));
    }

    private WebElement retrieve_input_endpoint(String component, String stream) {
        String componentId = components.getId(component);
        String endpointId = componentId + "-in-" + stream;
        return connector.retrieve_element(By.id(endpointId));
    }

    private WebElement retrieve_output_endpoint(String component, String stream) {
        String componentId = components.getId(component);
        String endpointId = componentId + "-out-" + stream;
        return connector.retrieve_element(By.id(endpointId));
    }

    private void create_new_topology(String topologyName, List<Component> components) {
        connector.open_and_wait(BASE_HTML_PATH + "topologies");
        connector.click("create-new-topology");
        connector.fill("topology-name-input", topologyName);

        for (Component component : components) {
            add_new_component(component.type, component.name, component.x, component.y);
        }

        connector.click("save-topology");
        String topologyId = connector.relative_location().replace("topologies/", "");
        topologies.storeId(topologyName, topologyId);
    }

    private void add_new_component(String type, String name, int x, int y) {
        connector.click(By.xpath("//*[contains(@class,'dropdown-toggle') and contains(text(),'" + type + "')]"));
        connector.click(linkText(name));

        components.storeId(name, retrieve_component_id(name));
        connector.drag_and_drop(retrieve_component(name), x - 50, y - 50);
        connector.click("save-topology");
    }

    static class Component {

        final String type;
        final String name;
        final int x;
        final int y;

        Component(String type, String name, int x, int y) {
            this.type = type;
            this.name = name;
            this.x = x;
            this.y = y;
        }

    }
}
