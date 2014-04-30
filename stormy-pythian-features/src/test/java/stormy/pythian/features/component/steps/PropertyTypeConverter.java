package stormy.pythian.features.component.steps;

import static org.apache.commons.lang.StringUtils.isBlank;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

@SuppressWarnings({ "rawtypes", "unchecked" })
public enum PropertyTypeConverter {
    STRING {
        @Override
        public Object convert(String type, String value) {
            return value;
        }
    },
    DOUBLE {
        @Override
        public Object convert(String type, String value) {
            return isBlank(value) ? null : Double.valueOf(value);
        }
    },
    INTEGER {
        @Override
        public Object convert(String type, String value) {
            return isBlank(value) ? null : Integer.valueOf(value);
        }
    },
    LONG {
        @Override
        public Object convert(String type, String value) {
            return isBlank(value) ? null : Long.valueOf(value);
        }
    },
    BOOLEAN {
        @Override
        public Object convert(String type, String value) {
            return isBlank(value) ? null : Boolean.valueOf(value);
        }
    },
    DATE {
        @Override
        public Object convert(String type, String value) {
            return DateTime.parse(value).toDate();
        }
    },
    ENUM {
        @Override
        public Object convert(String type, String value) {
            String enumName = type.split(":")[1];
            try {
                Class enumType = Class.forName(enumName);
                return Enum.valueOf(enumType, value);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("No enum found for name " + enumName);
            }
        }
    };

    public abstract Object convert(String type, String value);

    public Object convert(String value) {
        return convert(null, value);
    }

    public static PropertyTypeConverter from(String type) {
        if (StringUtils.startsWithIgnoreCase(type, ENUM.name() + ":")) {
            return ENUM;
        }

        for (PropertyTypeConverter converter : PropertyTypeConverter.values()) {
            if (StringUtils.equalsIgnoreCase(converter.name(), type)) {
                return converter;
            }
        }
        throw new IllegalArgumentException("No type converter found for " + type);
    }
}
