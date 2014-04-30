package stormy.pythian.model.instance;

public class InstanceTestBuilder {

    private Instance instance = new Instance();

    public static InstanceTestBuilder instance() {
        return new InstanceTestBuilder();
    }

    public InstanceTestBuilder label(Feature<?> label) {
        instance.label = label;
        return this;
    }

    public InstanceTestBuilder with(String name, Feature<?> feature) {
        instance.features.put(name, feature);
        return this;
    }

    public Instance build() {
        return instance;
    }
}
