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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import stormy.pythian.core.description.InputStreamDescription;
import stormy.pythian.model.annotation.MappingType;

public class InputStreamConfiguration {

    private InputStreamDescription description;

    private Map<String, String> mappings;
    private List<String> selectedFeatures;

    public InputStreamConfiguration() {
    }

    public InputStreamConfiguration(InputStreamDescription description, List<String> selectedFeatures) {
        this.description = description;
        this.selectedFeatures = selectedFeatures;
        this.mappings = null;
    }

    public InputStreamConfiguration(InputStreamDescription description, Map<String, String> mappings) {
        this.description = description;
        this.mappings = mappings;
        this.selectedFeatures = null;
    }

    public InputStreamDescription getDescription() {
        return description;
    }

    public Map<String, String> getMappings() {
        return mappings;
    }

    public List<String> getSelectedFeatures() {
        return selectedFeatures;
    }

    public Collection<String> getStreamFeatures() {
        switch (description.getType()) {
            case FIXED_FEATURES:
                return mappings.values();
            case USER_SELECTION:
                return selectedFeatures;
            default:
                throw new IllegalStateException("Mapping type " + description.getType() + " isn't supported!");
        }
    }

    public String getStreamName() {
        return description.getName();
    }

    public MappingType getMappingType() {
        return description.getType();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((mappings == null) ? 0 : mappings.hashCode());
        result = prime * result + ((selectedFeatures == null) ? 0 : selectedFeatures.hashCode());
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
        InputStreamConfiguration other = (InputStreamConfiguration) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (mappings == null) {
            if (other.mappings != null)
                return false;
        } else if (!mappings.equals(other.mappings))
            return false;
        if (selectedFeatures == null) {
            if (other.selectedFeatures != null)
                return false;
        } else if (!selectedFeatures.equals(other.selectedFeatures))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "InputStreamConfiguration [description=" + description + ", mappings=" + mappings
                + ", selectedFeatures=" + selectedFeatures + "]";
    }

}
