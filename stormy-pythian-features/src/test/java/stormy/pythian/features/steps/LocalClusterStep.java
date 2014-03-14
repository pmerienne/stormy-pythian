package stormy.pythian.features.steps;

import static org.fest.assertions.Assertions.assertThat;
import static stormy.pythian.features.support.Environment.BASE_HTML_PATH;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import stormy.pythian.features.support.Topologies;
import stormy.pythian.features.support.WebConnector;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class LocalClusterStep {

    private final WebConnector connector;
    private final Topologies topologies;

    public LocalClusterStep(WebConnector connector, Topologies topologies) {
        this.connector = connector;
        this.topologies = topologies;
    }

    @Then("^I should see a navigation link to the local cluster page$")
    public void i_should_see_a_navigation_link_to_the_local_cluster_page() throws Throwable {
        WebElement local_cluster_link = connector.retrieve_element(By.linkText("Local"));
        assertThat(local_cluster_link).isNotNull();
        assertThat(local_cluster_link.getAttribute("href")).isEqualTo("#/clusters/local");
    }

    @Given("^I\'m on the local cluster page$")
    public void i_m_on_the_local_cluster_page() throws Throwable {
        connector.open(BASE_HTML_PATH + "clusters/local");
    }

    @When("^I visit the local cluster page$")
    public void i_visit_the_local_cluster_page() throws Throwable {
        connector.open(BASE_HTML_PATH + "clusters/local");
    }

    @When("^I click on the choose topology to deploy button$")
    public void i_click_on_the_deploy_topology_button() throws Throwable {
        connector.click(By.id("choose-topology-to-deploy-button"));
    }

    @When("^I select the \"([^\"]*)\" topology$")
    public void i_select_the(String name) throws Throwable {
        connector.click(By.linkText(name));
    }

    @Then("^I should see the \"([^\"]*)\" deployed$")
    public void i_should_see_the_running(String name) throws Throwable {
        String topologyId = topologies.getId(name);
        String state_element_id = topologyId + "-state";
        WebElement state_element = connector.retrieve_element(By.id(state_element_id));

        assertThat(state_element).isNotNull();
        assertThat(state_element.getText()).isEqualTo("Deployed");
    }

    @Given("^a deployed topology \"([^\"]*)\"$")
    public void a_deplyed_topology(String name) throws Throwable {
        topologies.create_new_topology(name);

        connector.open(BASE_HTML_PATH + "clusters/local");
        connector.click(By.id("choose-topology-to-deploy-button"));
        connector.click(By.linkText(name));
    }

    @When("^I click on the undeploy button of \"([^\"]*)\"$")
    public void i_click_on_the_stop_button_of(String name) throws Throwable {
        String topologyId = topologies.getId(name);
        connector.click(By.id("undeploy-" + topologyId));
    }

    @Then("^I should see the \"([^\"]*)\" undeploying$")
    public void i_should_see_the_undeploying(String name) throws Throwable {
        String topologyId = topologies.getId(name);
        String state_element_id = topologyId + "-state";
        WebElement state_element = connector.retrieve_element(By.id(state_element_id));

        assertThat(state_element).isNotNull();
        assertThat(state_element.getText()).isEqualTo("Undeploying");
    }

    @Given("^an undeployed topology \"([^\"]*)\"$")
    public void a_stopped_topology(String name) throws Throwable {
        topologies.create_new_topology(name);
    }

    @Then("^I should not see the \"([^\"]*)\" deploy state$")
    public void i_should_not_see_the_deployed(String name) throws Throwable {
        String topologyId = topologies.getId(name);
        String state_element_id = topologyId + "-state";
        assertThat(connector.element_exists(By.id(state_element_id))).isFalse();
    }
}
