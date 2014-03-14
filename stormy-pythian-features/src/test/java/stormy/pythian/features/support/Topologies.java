package stormy.pythian.features.support;

import static org.openqa.selenium.By.linkText;
import static stormy.pythian.features.support.Environment.BASE_HTML_PATH;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import cucumber.api.java.After;

public class Topologies {

    private final Map<String, String> topologies;
    private final WebConnector connector;
    private final Components components;

    public Topologies(WebConnector connector, Components components) {
        this.connector = connector;
        this.components = components;
        this.topologies = new HashMap<>();
    }

    private void storeId(String topologyName, String topologyId) {
        this.topologies.put(topologyName, topologyId);
    }

    public String getId(String topologyName) {
        return topologies.get(topologyName);
    }

    public void create_new_topology(String topologyName) {
        connector.open(BASE_HTML_PATH + "topologies");
        connector.click("create-new-topology");
        connector.fill("topology-name-input", topologyName);

        connector.click("save-topology");
        String topologyId = connector.relative_location().replace("topologies/", "");
        storeId(topologyName, topologyId);
    }

    public void create_new_topology(String topologyName, List<Component> components) {
        connector.open(BASE_HTML_PATH + "topologies");
        connector.click("create-new-topology");
        connector.fill("topology-name-input", topologyName);

        for (Component component : components) {
            add_new_component(component.type, component.name, component.x, component.y);
        }

        connector.click("save-topology");
        String topologyId = connector.relative_location().replace("topologies/", "");
        storeId(topologyName, topologyId);
    }

    public void add_new_component(String type, String name, int x, int y) {
        connector.click(By.xpath("//*[contains(@class,'dropdown-toggle') and contains(text(),'" + type + "')]"));
        connector.click(linkText(name));

        components.storeId(name, retrieve_component_id(name));
        connector.drag_and_drop(retrieve_component(name), x - 50, y - 50);
        connector.click("save-topology");
    }

    public void connect(String component1Name, String component1Stream, String component2Name, String component2Stream) throws Throwable {
        WebElement outputEndpoint = retrieve_output_endpoint(component1Name, component1Stream);
        WebElement inputEndpoint = retrieve_input_endpoint(component2Name, component2Stream);
        connector.drag_and_drop(outputEndpoint, inputEndpoint);
        connector.click("save-topology");
    }

    public String retrieve_component_id(String componentName) {
        WebElement component = connector.retrieve_element(By.xpath("//*[contains(@class,'diagram-component-title') and contains(text(),'" + componentName + "')]/.."));
        String id = component.getAttribute("id");
        return id;
    }

    public WebElement retrieve_component(String name) {
        String componentId = components.getId(name);
        return connector.retrieve_element(By.id(componentId));
    }

    public WebElement retrieve_input_endpoint(String component, String stream) {
        String componentId = components.getId(component);
        String endpointId = componentId + "-in-" + stream;
        return connector.retrieve_element(By.id(endpointId));
    }

    public WebElement retrieve_output_endpoint(String component, String stream) {
        String componentId = components.getId(component);
        String endpointId = componentId + "-out-" + stream;
        return connector.retrieve_element(By.id(endpointId));
    }

    @After
    public void clean() {
        this.topologies.clear();
    }

}
