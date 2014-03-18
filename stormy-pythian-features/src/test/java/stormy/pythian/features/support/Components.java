package stormy.pythian.features.support;

import static org.openqa.selenium.By.linkText;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import cucumber.api.java.After;

public class Components {

    private final Map<String, String> components;
    private final WebConnector connector;

    public Components(WebConnector connector) {
        this.components = new HashMap<>();
        this.connector = connector;
    }

    public void storeId(String name, String id) {
        this.components.put(name, id);
    }

    public String getId(String name) {
        return components.get(name);
    }

    public void add_new_component(String type, String component, String name, int x, int y) {
        connector.click(By.xpath("//*[contains(@class,'dropdown-toggle') and contains(text(),'" + type + "')]"));
        connector.click(linkText(component));

        String component_id = retrieve_component_id(component);
        this.storeId(name, component_id);
        connector.drag_and_drop(component_id, x - 50, y - 50);
        connector.click("save-topology");

        connector.click(By.xpath("//*[contains(@class,'diagram-component-title') and contains(text(),'" + component + "')]"));
        connector.fill("component-name", name);
        connector.click("save-component");
        connector.click("save-topology");
    }

    public void set_properties(String componentName, List<Property> properties) {
        connector.click(By.xpath("//*[contains(@class,'diagram-component-title') and contains(text(),'" + componentName + "')]"));
        connector.click("properties-tab-heading");

        for (Property property : properties) {
            set_property(property);
        }

        connector.click("save-component");
        connector.click("save-topology");
    }

    private void set_property(Property property) {
        String propertyName = property.name.replaceAll(" ", "");
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

    public String get_property_value(String componentName, Property.Type type, String propertyName) {
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

    public WebElement retrieve_component(String name) {
        String componentId = this.getId(name);
        return connector.retrieve_element(By.id(componentId));
    }

    private String retrieve_component_id(String componentName) {
        WebElement component = connector.retrieve_element(By.xpath("//*[contains(@class,'diagram-component-title') and contains(text(),'" + componentName + "')]/.."));
        String id = component.getAttribute("id");
        return id;
    }

    @After
    public void clean() {
        this.components.clear();
    }

}
