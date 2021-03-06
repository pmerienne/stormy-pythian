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
import java.util.List;

@SuppressWarnings("serial")
public class ListedFeaturesMapper implements Serializable {

    private final List<String> selectedFeatures;

    public ListedFeaturesMapper(List<String> selectedFeatures) {
        this.selectedFeatures = selectedFeatures;
    }

    List<String> getSelectedFeatures() {
        return selectedFeatures;
    }

    public int size() {
        return selectedFeatures.size();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        ListedFeaturesMapper other = (ListedFeaturesMapper) obj;
        if (selectedFeatures == null) {
            if (other.selectedFeatures != null)
                return false;
        } else if (!selectedFeatures.equals(other.selectedFeatures))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ListedFeaturesMapper [selectedFeatures=" + selectedFeatures + "]";
    }

}
