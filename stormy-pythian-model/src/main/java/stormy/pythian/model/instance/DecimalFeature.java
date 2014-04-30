package stormy.pythian.model.instance;

import static stormy.pythian.model.instance.FeatureType.DECIMAL;

@SuppressWarnings("serial")
public class DecimalFeature extends Feature<Double> {

    public DecimalFeature(Double value) {
        super(DECIMAL, value);
    }

    @Override
    public DecimalFeature toDecimal() {
        return new DecimalFeature(value);
    }

    @Override
    public IntegerFeature toInteger() {
        return new IntegerFeature(value == null ? null : value.longValue());
    }

    @Override
    public BooleanFeature toBoolean() {
        return new BooleanFeature(value == null ? null : value > 0.0);
    }

    @Override
    public CategoricalFeature toCategorical() {
        throw new UnsupportedOperationException("Decimal feature can't be converted to categorical feature");
    }

    @Override
    public TextFeature toText() {
        return new TextFeature(value == null ? null : value.toString());
    }

    @Override
    public DateFeature toDate() {
        throw new UnsupportedOperationException("Decimal feature can't be converted to categorical date");
    }

}
