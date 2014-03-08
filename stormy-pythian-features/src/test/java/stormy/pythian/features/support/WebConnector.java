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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static stormy.pythian.features.support.Environment.BASE_HTML_PATH;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
		clickAndWait(By.id(selector));
	}

	public void fill(String id, String text) {
		WebElement element = driver.findElementById(id);
		element.sendKeys(text);
	}

	public void clickAndWait(By by) {
		WebElement element = driver.findElement(by);
		element.click();
		driver.manage().timeouts().implicitlyWait(DEFAULT_TIMEOUT, MILLISECONDS);
		waitForAngularRequestsToFinish();
	}

	public boolean elementExists(String id) {
		try {
			WebElement element = driver.findElementById(id);
			return element != null;
		} catch (NoSuchElementException ex) {
			return false;
		}
	}

	public void openAndWait(String location) {
		driver.get(location);
		driver.manage().timeouts().implicitlyWait(DEFAULT_TIMEOUT, MILLISECONDS);
		waitForAngularRequestsToFinish();
	}

	public boolean navigationBarBrandContains(String text) {
		WebElement content = driver.findElement(By.className("navbar-brand"));
		return content.getText().contains(text);
	}

	public PhantomJSDriver getDriver() {
		return driver;
	}

	public String getRelativeLocation() {
		return driver.getCurrentUrl().replaceAll(BASE_HTML_PATH, "");
	}

	public void waitForAngularRequestsToFinish() {
		driver.executeAsyncScript("var callback = arguments[arguments.length - 1];" +
				"angular.element(document.body).injector().get('$browser').notifyWhenNoOutstandingRequests(callback);");
	}

}
