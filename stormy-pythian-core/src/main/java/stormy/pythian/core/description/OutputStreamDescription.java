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

import static org.apache.commons.lang.StringUtils.isBlank;
import java.util.ArrayList;
import java.util.List;
import stormy.pythian.model.annotation.MappingType;

public class OutputStreamDescription {

    private String name;
    private String from;
    private List<FeatureDescription> newFeatures = new ArrayList<>();

    private final MappingType type;

    public OutputStreamDescription() {
        this.name = null;
        this.from = null;
        this.newFeatures = null;
        this.type = null;
    }

    public OutputStreamDescription(String name, String from, MappingType type, List<FeatureDescription> newFeatures) {
        this.name = name;
        this.from = from;
        this.newFeatures = newFeatures;
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setNewFeatures(List<FeatureDescription> newFeatures) {
        this.newFeatures = newFeatures;
    }

    public String getName() {
        return name;
    }

    public String getFrom() {
        return from;
    }

    public List<FeatureDescription> getNewFeatures() {
        return newFeatures;
    }

    public MappingType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((isBlank(from)) ? 0 : from.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((isBlank(name)) ? 0 : name.hashCode());
        result = prime * result + ((newFeatures == null) ? 0 : newFeatures.hashCode());
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
        OutputStreamDescription other = (OutputStreamDescription) obj;
        if (isBlank(from)) {
            if (!isBlank(other.from))
                return false;
        } else if (!from.equals(other.from))
            return false;
        if (type != other.type)
            return false;
        if (isBlank(name)) {
            if (!isBlank(other.name))
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (newFeatures == null) {
            if (other.newFeatures != null)
                return false;
        } else if (!newFeatures.equals(other.newFeatures))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "OutputStreamDescription [name=" + name + ", from=" + from + ", newFeatures=" + newFeatures + ", mappingType=" + type + "]";
    }

}
