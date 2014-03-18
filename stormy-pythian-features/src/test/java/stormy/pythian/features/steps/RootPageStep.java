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
package stormy.pythian.features.steps;

import static org.fest.assertions.Assertions.assertThat;
import static stormy.pythian.features.support.Environment.BASE_PATH;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import stormy.pythian.features.support.WebConnector;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class RootPageStep {

    private final WebConnector connector;

    public RootPageStep(WebConnector connector) {
        this.connector = connector;
    }

    @Given("^I'm on the root page$")
    public void i_m_on_the_root_page() throws Throwable {
        connector.open(BASE_PATH);
    }

    @When("^I visit the root page$")
    public void i_visit_the_root_page() throws Throwable {
        connector.open(BASE_PATH);
    }

    @Then("^I should see the brand name : \"([^\"]*)\"")
    public void i_should_see_the_brand_name(String content) {
        WebElement navbar_element = connector.retrieve_element(By.className("navbar-brand"));
        assertThat(navbar_element.getText()).contains(content);
    }

    @When("^I click on the navigation link \"([^\"]*)\"$")
    public void I_click_on_the_navigation_link(String text) throws Throwable {
        connector.click(By.linkText(text));
    }

}
