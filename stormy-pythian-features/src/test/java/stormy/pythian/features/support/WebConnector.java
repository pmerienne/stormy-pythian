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
package stormy.pythian.features.support;

import static java.util.concurrent.TimeUnit.SECONDS;
import static stormy.pythian.features.support.Environment.BASE_HTML_PATH;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class WebConnector {

    private final static boolean HEADLESS = true;

    private final static RemoteWebDriver driver;

    /**
     * Static init to avoid lots of initialization causing bugs and timeouts!
     */
    static {
        if (HEADLESS) {
            driver = new PhantomJSDriver();
        } else {
            System.setProperty("webdriver.chrome.driver", "/opt/chromedriver/chromedriver");
            driver = new ChromeDriver();
        }

        driver.manage().timeouts().implicitlyWait(1, SECONDS);
        driver.manage().timeouts().setScriptTimeout(2, SECONDS);
    }

    public void click(String selector) {
        click(By.id(selector));
    }

    public void fill(String id, String text) {
        WebElement element = driver.findElementById(id);
        element.sendKeys(text);
    }

    public void click(By by) {
        WebElement element = driver.findElement(by);
        element.click();
        wait_for_angular_requests_to_finish();
    }

    public void drag_and_drop(WebElement source, WebElement destination) {
        new Actions(driver)
                .clickAndHold(source)
                .moveByOffset(1, 1)
                .moveToElement(destination)
                .moveByOffset(1, 1)
                .release()
                .build().perform();
        wait_for_angular_requests_to_finish();
    }

    public void drag_and_drop(WebElement source, int dx, int dy) {
        new Actions(driver)
                .clickAndHold(source)
                .moveByOffset(dx, dy)
                .moveByOffset(1, 1)
                .release()
                .build().perform();

        wait_for_angular_requests_to_finish();
    }

    public WebElement retrieve_element(By by) {
        try {
            return driver.findElement(by);
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    public boolean element_exists(String id) {
        return element_exists(By.id(id));
    }

    public boolean element_exists(By by) {
        try {
            WebElement element = driver.findElement(by);
            return element != null;
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    public void open(String location) {
        driver.get(location);
        wait_for_angular_requests_to_finish();
    }

    public boolean navigation_bar_brand_contains(String text) {
        WebElement content = driver.findElement(By.className("navbar-brand"));
        return content.getText().contains(text);
    }

    public RemoteWebDriver driver() {
        return driver;
    }

    public String relative_location() {
        return driver.getCurrentUrl().replaceAll(BASE_HTML_PATH, "");
    }

    public void wait_for_angular_requests_to_finish() {
        driver.executeAsyncScript("var callback = arguments[arguments.length - 1];" +
                "angular.element(document.body).injector().get('$browser').notifyWhenNoOutstandingRequests(callback);");
    }

    public Object executeScript(String script, Object... args) {
        return driver.executeScript(script, args);
    }
}
