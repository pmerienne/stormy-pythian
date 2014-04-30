package stormy.pythian.model.instance;

import static stormy.pythian.model.instance.FeatureType.CATEGORICAL;

@SuppressWarnings("serial")
public class CategoricalFeature extends Feature<String> {

    public CategoricalFeature(String value) {
        super(CATEGORICAL, value);
    }

    @Override
    public DecimalFeature toDecimal() {
        throw new UnsupportedOperationException("Categorical feature can't be converted to decimal feature");
    }

    @Override
    public IntegerFeature toInteger() {
        throw new UnsupportedOperationException("Categorical feature can't be converted to integer feature");
    }

    @Override
    public BooleanFeature toBoolean() {
        throw new UnsupportedOperationException("Categorical feature can't be converted to decimal feature");
    }

    @Override
    public CategoricalFeature toCategorical() {
        return new CategoricalFeature(value);
    }

    @Override
    public TextFeature toText() {
        return new TextFeature(value);
    }

    @Override
    public DateFeature toDate() {
        throw new UnsupportedOperationException("Categorical feature can't be converted to date feature");
    }

}
