package stormy.pythian.model.instance;

import static stormy.pythian.model.instance.FeatureType.INTEGER;
import java.util.Date;

@SuppressWarnings("serial")
public class IntegerFeature extends Feature<Long> {

    public IntegerFeature(Long value) {
        super(INTEGER, value);
    }

    @Override
    public DecimalFeature toDecimal() {
        return new DecimalFeature(value == null ? null : value.doubleValue());
    }

    @Override
    public IntegerFeature toInteger() {
        return new IntegerFeature(value);
    }

    @Override
    public BooleanFeature toBoolean() {
        return new BooleanFeature(value == null ? null : value > 0);
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
        return new DateFeature(value == null ? null : new Date(value));
    }

}
