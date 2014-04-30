package stormy.pythian.model.instance;

import org.apache.commons.lang.StringUtils;

public enum FeatureType {
    DECIMAL, INTEGER, BOOLEAN, CATEGORICAL, TEXT, DATE, ANY;

    public static FeatureType from(String name) {
        return FeatureType.valueOf(StringUtils.upperCase(name));
    }
}
