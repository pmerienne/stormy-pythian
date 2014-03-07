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
package stormy.pythian.features.connectors;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import cucumber.api.java.After;
import cucumber.api.java.Before;

public class WebConnector {

    private final static long DEFAULT_TIMEOUT = 2000;
    private PhantomJSDriver driver;

    @Before
    public void initSelenium() throws Exception {
        driver = new PhantomJSDriver();
    }

    @After
    public void destroySelenium() {
        driver.close();
    }

    public void clickAndWait(String selector) {
        WebElement element = driver.findElement(By.id(selector));
        element.click();
        driver.manage().timeouts().implicitlyWait(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void openAndWait(String location) {
        driver.get(location);
        driver.manage().timeouts().implicitlyWait(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public boolean isTextPresent(String text) {
        WebElement content = driver.findElement(By.className("navbar-brand"));
        return content.getText().contains(text);
    }
}
