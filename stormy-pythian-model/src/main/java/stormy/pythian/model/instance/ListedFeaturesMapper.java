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
