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
import static stormy.pythian.features.web.support.Environment.BASE_HTML_PATH;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import stormy.pythian.features.web.support.Clusters;
import stormy.pythian.features.web.support.Topologies;
import stormy.pythian.features.web.support.WebConnector;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class LocalClusterStep {

    private final WebConnector connector;
    private final Topologies topologies;
    private final Clusters clusters;

    public LocalClusterStep(WebConnector connector, Topologies topologies, Clusters clusters) {
        this.connector = connector;
        this.topologies = topologies;
        this.clusters = clusters;
    }

    @Then("^I should see a navigation link to the local cluster page$")
    public void i_should_see_a_navigation_link_to_the_local_cluster_page() throws Throwable {
        WebElement local_cluster_link = connector.retrieve_element(By.linkText("Local"));
        assertThat(local_cluster_link).isNotNull();
        assertThat(local_cluster_link.getAttribute("href").replace(BASE_HTML_PATH, "")).isEqualTo("clusters/local");
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
        String state_element_id = topologyId + "-status";
        WebElement state_element = connector.retrieve_element(By.id(state_element_id));

        assertThat(state_element).isNotNull();
        assertThat(state_element.getText()).isEqualToIgnoringCase("Deployed");
    }

    @Given("^a deployed topology \"([^\"]*)\"$")
    public void a_deplyed_topology(String name) throws Throwable {
        String topology_id = topologies.create_new_topology(name);

        connector.open(BASE_HTML_PATH + "clusters/local");
        connector.click(By.id("choose-topology-to-deploy-button"));
        connector.click(By.linkText(name));
        
        clusters.register("local", topology_id);
    }

    @When("^I click on the undeploy button of \"([^\"]*)\"$")
    public void i_click_on_the_stop_button_of(String name) throws Throwable {
        String topologyId = topologies.getId(name);
        connector.click(By.id("undeploy-" + topologyId));
    }

    @Then("^I should see the \"([^\"]*)\" undeploying$")
    public void i_should_see_the_undeploying(String name) throws Throwable {
        String topologyId = topologies.getId(name);
        String state_element_id = topologyId + "-status";
        WebElement state_element = connector.retrieve_element(By.id(state_element_id));

        assertThat(state_element).isNotNull();
        assertThat(state_element.getText()).isEqualToIgnoringCase("Undeploying");
    }

    @Given("^an undeployed topology \"([^\"]*)\"$")
    public void a_stopped_topology(String name) throws Throwable {
        topologies.create_new_topology(name);
    }

    @Then("^I should not see the \"([^\"]*)\" deploy state$")
    public void i_should_not_see_the_deployed(String name) throws Throwable {
        String topologyId = topologies.getId(name);
        String state_element_id = topologyId + "-status";
        assertThat(connector.element_exists(By.id(state_element_id))).isFalse();
    }
}
