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
package stormy.pythian.core.configuration;

import java.util.ArrayList;
import java.util.List;
import stormy.pythian.core.description.PythianStateDescription;
import stormy.pythian.model.component.PythianState;

public class PythianStateConfiguration implements Validable {

    private String name;

    private PythianStateDescription description;
    private List<PropertyConfiguration> properties = new ArrayList<>();

    public PythianStateConfiguration() {
    }

    public PythianStateConfiguration(String name, PythianStateDescription description, List<PropertyConfiguration> properties) {
        this.name = name;
        this.description = description;
        this.properties = properties;
    }

    @Override
    public ValidationResult validate() {
        ValidationResult result = new ValidationResult(name);

        for (PropertyConfiguration property : properties) {
            result.include(property.validate());
        }

        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PythianStateDescription getDescription() {
        return description;
    }

    public List<PropertyConfiguration> getProperties() {
        return properties;
    }

    public Class<? extends PythianState> retrieveImplementation() {
        return description.getClazz();
    }

    public void setDescription(PythianStateDescription description) {
        this.description = description;
    }

    public void setProperties(List<PropertyConfiguration> properties) {
        this.properties = properties;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PythianStateConfiguration other = (PythianStateConfiguration) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PythianStateConfiguration [name=" + name + ", description=" + description + ", properties=" + properties + "]";
    }

}
