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

import static org.junit.Assert.assertTrue;
import stormy.pythian.features.connectors.WebConnector;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class RootPageStepDef {

    private final WebConnector connector;

    public RootPageStepDef(WebConnector connector) {
        this.connector = connector;
    }

    @When("^I visit the root page$")
    public void i_visit_the_root_page() throws Throwable {
        connector.openAndWait("http://localhost:8080/stormy-pythian-web/");
    }

    @Then("^I should see \"([^\"]*)\"")
    public void i_should_see(String content) {
        assertTrue(connector.isTextPresent(content));
    }

}
