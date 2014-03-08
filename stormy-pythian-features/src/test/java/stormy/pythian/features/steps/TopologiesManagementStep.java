package stormy.pythian.features.steps;

import static org.fest.assertions.Assertions.assertThat;
import static stormy.pythian.features.support.Environment.BASE_HTML_PATH;
import stormy.pythian.features.support.Topologies;
import stormy.pythian.features.support.WebConnector;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class TopologiesManagementStep {

    private final WebConnector connector;
    private final Topologies topologies;

    public TopologiesManagementStep(WebConnector connector, Topologies topologies) {
        this.connector = connector;
        this.topologies = topologies;
    }

    @Given("^I'm on the topologies page$")
    public void i_m_on_the_topologies_page() throws Throwable {
        connector.open_and_wait(BASE_HTML_PATH + "topologies");
    }

    @When("^I click on the create new topology button$")
    public void i_click_on_the_create_new_topology_button() throws Throwable {
        connector.click("create-new-topology");
    }

    @Then("^I should be redirected to the edit topology page$")
    public void i_should_be_redirected_to_the_edit_topology_page() throws Throwable {
        assertThat(connector.relative_location()).matches("topologies/.+");
    }

    @Given("^an empty topology named \"([^\"]*)\"$")
    public void an_empty_topology_named(String topologyName) throws Throwable {
        connector.open_and_wait(BASE_HTML_PATH + "topologies");
        connector.click("create-new-topology");
        connector.fill("topology-name-input", topologyName);
        connector.click("save-topology");

        String topologyId = connector.relative_location().replace("topologies/", "");
        topologies.storeId(topologyName, topologyId);
    }

    @When("^I click on the edit topology button of \"([^\"]*)\"$")
    public void i_click_on_the_edit_topology_button_of(String topologyName) throws Throwable {
        String topologyId = topologies.getId(topologyName);
        connector.click("edit-topology-" + topologyId);
    }

    @When("^I click on the delete topology button of \"([^\"]*)\"$")
    public void i_click_on_the_delete_topology_button_of(String topologyName) throws Throwable {
        String topologyId = topologies.getId(topologyName);
        connector.click("delete-topology-" + topologyId);
    }

    @Then("^I should not see \"([^\"]*)\" in the topologies list$")
    public void i_should_not_see_the_Topology_in_the_topologies_list(String topologyName) throws Throwable {
        String topologyId = topologies.getId(topologyName);
        assertThat(connector.element_exists("topology-" + topologyId + "-name")).isFalse();
    }

    @When("^I visit the topologies page$")
    public void i_visit_the_topologies_page() throws Throwable {
        connector.open_and_wait(BASE_HTML_PATH + "topologies");
    }

    @Then("^I should see \"([^\"]*)\" in the topologies list$")
    public void i_should_see_a_topology_in_the_topologies_list(String topologyName) throws Throwable {
        String topologyId = topologies.getId(topologyName);
        assertThat(connector.element_exists("topology-" + topologyId + "-name")).isTrue();
    }

    @Given("^I'm on the \"([^\"]*)\" edition page$")
    public void i_m_on_the_edition_page(String topologyName) throws Throwable {
        String topologyId = topologies.getId(topologyName);
        connector.open_and_wait(BASE_HTML_PATH + "topologies/" + topologyId);
    }
}
