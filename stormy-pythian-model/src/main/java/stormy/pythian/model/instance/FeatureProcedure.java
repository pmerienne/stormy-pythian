package stormy.pythian.model.instance;

import java.io.Serializable;

public interface FeatureProcedure extends Serializable {

	void process(Feature<?> feature);
}
