package stormy.pythian.model.instance;

import java.io.Serializable;

public interface FeatureFunction extends Serializable {

	<T> Feature<T> transform(Feature<T> feature);
}
