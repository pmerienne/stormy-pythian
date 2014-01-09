package stormy.pythian.model.instance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeaturesIndex implements Serializable {

	private static final long serialVersionUID = 4675835465674004328L;

	private final Map<String, Integer> mapping;

	public FeaturesIndex(Map<String, Integer> mapping) {
		this.mapping = mapping;
	}

	public Integer getIndex(String featureName) {
		return mapping.get(featureName);
	}

	public static class Builder {

		private final Map<String, Integer> mapping;

		public Builder() {
			mapping = new HashMap<>();
		}

		public Builder(Map<String, Integer> mapping) {
			this.mapping = new HashMap<>(mapping);
		}

		public static Builder featuresIndex() {
			return new Builder();
		}

		public static Builder from(FeaturesIndex index) {
			return new Builder(index.mapping);
		}

		public Builder with(String feature) {
			mapping.put(feature, mapping.size() + 1);
			return this;
		}

		public Builder with(List<String> features) {
			for (String feature : features) {
				with(feature);
			}
			return this;
		}

		public FeaturesIndex build() {
			return new FeaturesIndex(mapping);
		}
	}
}
