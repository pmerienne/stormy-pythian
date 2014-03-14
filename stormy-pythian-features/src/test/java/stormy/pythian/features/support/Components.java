package stormy.pythian.features.support;

import java.util.HashMap;
import java.util.Map;
import cucumber.api.java.After;

public class Components {

    private final Map<String, String> components;

    public Components() {
        this.components = new HashMap<>();
    }

    public void storeId(String name, String id) {
        this.components.put(name, id);
    }

    public String getId(String name) {
        return components.get(name);
    }

    @After
    public void clean() {
        this.components.clear();
    }
}
