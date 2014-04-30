package stormy.pythian.model.instance;

import static stormy.pythian.model.instance.FeatureType.BOOLEAN;

@SuppressWarnings("serial")
public class BooleanFeature extends Feature<Boolean> {

    public BooleanFeature(Boolean value) {
        super(BOOLEAN, value);
    }

    @Override
    public DecimalFeature toDecimal() {
        if (value == null) {
            return new DecimalFeature(null);
        } else {
            return new DecimalFeature(value ? 1.0 : 0.0);
        }
    }

    @Override
    public IntegerFeature toInteger() {
        if (value == null) {
            return new IntegerFeature(null);
        } else {
            return new IntegerFeature(value ? 1L : 0L);
        }
    }

    @Override
    public BooleanFeature toBoolean() {
        return new BooleanFeature(value);
    }

    @Override
    public CategoricalFeature toCategorical() {
        return new CategoricalFeature(value == null ? null : value.toString());
    }

    @Override
    public TextFeature toText() {
        return new TextFeature(value == null ? null : value.toString());
    }

    @Override
    public DateFeature toDate() {
        throw new UnsupportedOperationException("Boolean feature can't be converted to date feature");
    }

}
