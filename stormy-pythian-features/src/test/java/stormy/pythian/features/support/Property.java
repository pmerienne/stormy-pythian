package stormy.pythian.features.support;

public class Property {

    public final String name;
    public final Type type;
    public final String value;

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
        this.type = null;
    }

    public Property(String name, Type type, String value) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public static enum Type {
        String, Integer, Double, Boolean, Enum, Decimal
    }
}
