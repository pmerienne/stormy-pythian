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

    public String create_new_topology(String topologyName) {
        connector.open(BASE_HTML_PATH + "topologies");
        connector.click("create-new-topology");
        connector.fill("topology-name-input", topologyName);

        connector.click("save-topology");
        String topologyId = connector.relative_location().replace("topologies/", "");
        storeId(topologyName, topologyId);
        return topologyId;
    }

    public void create_new_topology(String topologyName, List<Component> components) {
        connector.open(BASE_HTML_PATH + "topologies");
        connector.click("create-new-topology");
        connector.fill("topology-name-input", topologyName);

        for (Component component : components) {
            this.components.add_new_component(component.type, component.component, component.name, component.x, component.y);
        }

        connector.click("save-topology");
        String topologyId = connector.relative_location().replace("topologies/", "");
        storeId(topologyName, topologyId);
    }

    public void connect(String component1Name, String component1Stream, String component2Name, String component2Stream) throws Throwable {
        WebElement outputEndpoint = retrieve_output_endpoint(component1Name, component1Stream);
        WebElement inputEndpoint = retrieve_input_endpoint(component2Name, component2Stream);
        connector.drag_and_drop(outputEndpoint, inputEndpoint);
        connector.click("save-topology");
    }

    public WebElement retrieve_input_endpoint(String name, String stream) {
        String componentId = components.getId(name);
        String endpointId = componentId + "-in-" + stream;
        return connector.retrieve_element(By.id(endpointId));
    }

    public WebElement retrieve_output_endpoint(String name, String stream) {
        String componentId = components.getId(name);
        String endpointId = componentId + "-out-" + stream;
        return connector.retrieve_element(By.id(endpointId));
    }

    @After
    public void clean() {
        this.topologies.clear();
    }

}
