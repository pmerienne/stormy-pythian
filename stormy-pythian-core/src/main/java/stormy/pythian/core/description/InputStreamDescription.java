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
package stormy.pythian.core.description;

import java.util.ArrayList;
import java.util.List;
import stormy.pythian.model.annotation.MappingType;

public class InputStreamDescription {

    private String name;
    private MappingType type;
    private List<FeatureDescription> expectedFeatures = new ArrayList<>();
    private boolean mandatory = true;

    public InputStreamDescription() {
        this.name = null;
        this.type = null;
        this.expectedFeatures = null;
    }

    public InputStreamDescription(String name, MappingType type) {
        this.name = name;
        this.type = type;
        this.expectedFeatures = new ArrayList<>();
    }

    public InputStreamDescription(String name, MappingType type, List<FeatureDescription> expectedFeatures) {
        this.name = name;
        this.type = type;
        this.expectedFeatures = expectedFeatures;
    }

    public InputStreamDescription(String name, MappingType type, List<FeatureDescription> expectedFeatures, boolean mandatory) {
        this.name = name;
        this.type = type;
        this.expectedFeatures = expectedFeatures;
        this.mandatory = mandatory;
    }

    public String getName() {
        return name;
    }

    public MappingType getType() {
        return type;
    }

    public List<FeatureDescription> getExpectedFeatures() {
        return expectedFeatures;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(MappingType type) {
        this.type = type;
    }

    public void setExpectedFeatures(List<FeatureDescription> expectedFeatures) {
        this.expectedFeatures = expectedFeatures;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expectedFeatures == null) ? 0 : expectedFeatures.hashCode());
        result = prime * result + (mandatory ? 1231 : 1237);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        InputStreamDescription other = (InputStreamDescription) obj;
        if (expectedFeatures == null) {
            if (other.expectedFeatures != null)
                return false;
        } else if (!expectedFeatures.equals(other.expectedFeatures))
            return false;
        if (mandatory != other.mandatory)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "InputStreamDescription [name=" + name + ", type=" + type + ", expectedFeatures=" + expectedFeatures + ", mandatory=" + mandatory + "]";
    }

}
