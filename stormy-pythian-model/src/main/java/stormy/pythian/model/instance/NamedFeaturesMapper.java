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
