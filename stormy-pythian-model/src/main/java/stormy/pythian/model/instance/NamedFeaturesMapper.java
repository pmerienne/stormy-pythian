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
package stormy.pythian.model.instance;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class NamedFeaturesMapper implements Serializable {

    private final Map<String, String> mappings;

    public NamedFeaturesMapper(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    String getFeatureName(String featureName) {
        String realFeatureName = mappings.get(featureName);
        if (realFeatureName == null) {
            throw new IllegalArgumentException("No feature found for " + featureName);
        }
        return realFeatureName;
    }
    
    public boolean isSet(String featureName) {
        return mappings.containsKey(featureName);
    }

    public int size() {
        return mappings.size();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mappings == null) ? 0 : mappings.hashCode());
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
        NamedFeaturesMapper other = (NamedFeaturesMapper) obj;
        if (mappings == null) {
            if (other.mappings != null)
                return false;
        } else if (!mappings.equals(other.mappings))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "NamedFeaturesMapper [mappings=" + mappings + "]";
    }

}
