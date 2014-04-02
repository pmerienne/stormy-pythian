package stormy.pythian.features.component.steps;

import cucumber.api.java.en.When;

public class TimeSteps {

    @When("^I wait (\\d+) seconds?$")
    public void i_wait_seconds(int seconds) throws Throwable {
        Thread.sleep(seconds * 1000);
    }
}
