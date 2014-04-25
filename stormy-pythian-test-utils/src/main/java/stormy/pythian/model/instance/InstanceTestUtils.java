package stormy.pythian.model.instance;

public class InstanceTestUtils {

    @SuppressWarnings("unchecked")
    public static <T> T get(Instance instance, String featureName) {
        return (T) instance.features.get(featureName);
    }
}
