package stormy.pythian.model.instance;

public interface FeatureFunction {

	<T> Feature<T> transform(Feature<T> feature);
}
