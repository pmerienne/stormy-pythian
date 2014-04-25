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

import static org.apache.commons.lang.StringUtils.isEmpty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import stormy.pythian.core.description.OutputStreamDescription;
import stormy.pythian.model.annotation.MappingType;

public class OutputStreamConfiguration {

    private OutputStreamDescription description;

    private Map<String, String> mappings = new HashMap<>();
    private List<String> selectedFeatures = new ArrayList<>();

    public OutputStreamConfiguration() {
        this.description = null;
        this.mappings = null;
        this.selectedFeatures = null;
    }

    public OutputStreamConfiguration(OutputStreamDescription description, Map<String, String> mappings) {
        this.description = description;
        this.mappings = mappings;
        this.selectedFeatures = null;
    }

    public OutputStreamConfiguration(OutputStreamDescription description, List<String> selectedFeatures) {
        this.description = description;
        this.mappings = null;
        this.selectedFeatures = selectedFeatures;
    }

    public OutputStreamDescription getDescription() {
        return description;
    }

    public void setDescription(OutputStreamDescription description) {
        this.description = description;
    }

    public List<String> getSelectedFeatures() {
        return selectedFeatures;
    }

    public void setSelectedFeatures(List<String> selectedFeatures) {
        this.selectedFeatures = selectedFeatures;
    }

    public void setMappings(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    public String retrieveInputStreamSource() {
        return description.getFrom();
    }

    public boolean hasInputStreamSource() {
        return !isEmpty(description.getFrom());
    }

    public String retrieveStreamName() {
        return description.getName();
    }

    public MappingType retrieveMappingType() {
        return description.getType();
    }

    public Map<String, String> getMappings() {
        return mappings;
    }

    public List<String> retrieveNewFeatures() {
        switch (description.getType()) {
            case NAMED:
                return new ArrayList<>(mappings.values());
            case LISTED:
                return selectedFeatures;
            default:
                throw new IllegalStateException("Mapping type " + description.getType() + " isn't supported!");
        }
    }
}
