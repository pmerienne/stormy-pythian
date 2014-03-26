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

import static org.apache.commons.lang.StringUtils.remove;
import static org.openqa.selenium.Keys.ESCAPE;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class States {

    private final WebConnector connector;

    public States(WebConnector connector) {
        this.connector = connector;
    }

    public void go_to_state_view(String componentName, String stateName) {
        connector.click(By.xpath("//*[contains(@class,'diagram-component-title') and contains(text(),'" + componentName + "')]"));
        connector.click("state-" + remove(stateName, " ") + "-tab-heading");
    }

    public void save_and_close() {
        connector.click("save-component");
        connector.click("save-topology");
    }

    public void close() {
        connector.press(ESCAPE);
    }

    public void set_state_description(String descriptionName) {
        connector.select(By.id("state-description"), descriptionName);
    }

    public void set_properties(List<Property> properties) {
        for (Property property : properties) {
            set_property(property);
        }
    }

    private void set_property(Property property) {
        String propertyName = remove(property.name, " ");
        WebElement element = connector.retrieve_element(By.xpath("//input[contains(@name,'" + propertyName + "')]"));
        switch (property.type) {
            case Boolean:
                String actualValue = element.getAttribute("checked");
                if (!StringUtils.equals(actualValue, property.value)) {
                    WebElement switchElement = connector.retrieve_element(By.xpath("//div[contains(@name, '" + propertyName + "')]/div"));
                    connector.click(switchElement);
                }
                break;
            case Enum:
                connector.select(By.xpath("//select[contains(@name,'" + propertyName + "')]"), property.value);
                break;
            default:
                connector.fill(By.xpath("//input[contains(@name,'" + propertyName + "')]"), property.value);
                break;
        }
    }

    public String get_property_value(Property.Type type, String propertyName) {
        String inputName = propertyName.replaceAll(" ", "");
        WebElement property_element = connector.retrieve_element(By.xpath("//input[contains(@name,'" + inputName + "')]"));

        String value = null;
        switch (type) {
            case Boolean:
                value = property_element.getAttribute("checked");
                break;
            case Enum:
                value = connector.retrieve_selected(By.xpath("//select[contains(@name,'" + inputName + "')]"));
                break;
            default:
                value = property_element.getAttribute("value");
                break;
        }

        return value;
    }

}
