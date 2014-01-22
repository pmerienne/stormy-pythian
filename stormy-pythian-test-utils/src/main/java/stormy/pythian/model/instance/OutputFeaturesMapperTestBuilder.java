package stormy.pythian.model.instance;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputFeaturesMapperTestBuilder {

	private final List<String> features = new ArrayList<>();
	private final Map<String, String> mappings = new HashMap<>();

	public static OutputFeaturesMapperTestBuilder outputFixedFeaturesMapper() {
		return new OutputFeaturesMapperTestBuilder();
	}

	public static OutputFeaturesMapperTestBuilder outputFixedFeaturesMapper(String... features) {
		OutputFeaturesMapperTestBuilder builder = new OutputFeaturesMapperTestBuilder();
		return builder.with(features);
	}

	public OutputFeaturesMapperTestBuilder with(String... features) {
		this.features.addAll(asList(features));
		return this;
	}

	public OutputFeaturesMapperTestBuilder map(String componentFeatureName, String instanceFeatureName) {
		this.mappings.put(componentFeatureName, instanceFeatureName);
		return this;
	}

	public OutputFeaturesMapper build() {
		return new OutputFeaturesMapper(new FeaturesIndex(features), mappings);
	}
}
