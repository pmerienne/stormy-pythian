package stormy.pythian.model.instance;

import static stormy.pythian.model.instance.FeatureType.TEXT;
import org.joda.time.DateTime;

@SuppressWarnings("serial")
public class TextFeature extends Feature<String> {

    public TextFeature(String value) {
        super(TEXT, value);
    }

    @Override
    public DecimalFeature toDecimal() {
        if (value == null) {
            return new DecimalFeature(null);
        }

        try {
            return new DecimalFeature(Double.parseDouble(value));
        } catch (NumberFormatException nfe) {
            throw new UnsupportedOperationException("Text feature '" + value + "' can't be converted to decimal feature", nfe);
        }
    }

    @Override
    public IntegerFeature toInteger() {
        if (value == null) {
            return new IntegerFeature(null);
        }

        try {
            return new IntegerFeature(Long.parseLong(value));
        } catch (NumberFormatException nfe) {
            throw new UnsupportedOperationException("Text feature '" + value + "' can't be converted to integer feature", nfe);
        }
    }

    @Override
    public BooleanFeature toBoolean() {
        if (value == null) {
            return new BooleanFeature(null);
        }

        return new BooleanFeature(Boolean.parseBoolean(value));
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
        if (value == null) {
            return new DateFeature(null);
        }

        try {
            return new DateFeature(DateTime.parse(value).toDate());
        } catch (IllegalArgumentException iae) {
            throw new UnsupportedOperationException("Text feature '" + value + "' can't be converted to date feature", iae);
        }
    }

}
