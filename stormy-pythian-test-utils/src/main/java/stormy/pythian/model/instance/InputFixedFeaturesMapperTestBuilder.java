package stormy.pythian.model.instance;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputFixedFeaturesMapperTestBuilder {

	private final List<String> features = new ArrayList<>();
	private final Map<String, String> mappings = new HashMap<>();

	public static InputFixedFeaturesMapperTestBuilder inputFixedFeaturesMapper() {
		return new InputFixedFeaturesMapperTestBuilder();
	}
	

	public static InputFixedFeaturesMapperTestBuilder inputFixedFeaturesMapper(String... features) {
		InputFixedFeaturesMapperTestBuilder builder = new InputFixedFeaturesMapperTestBuilder();
		return builder.with(features);
	}

	public InputFixedFeaturesMapperTestBuilder with(String... features) {
		this.features.addAll(asList(features));
		return this;
	}

	public InputFixedFeaturesMapperTestBuilder map(String componentFeatureName, String instanceFeatureName) {
		this.mappings.put(componentFeatureName, instanceFeatureName);
		return this;
	}

	public InputFixedFeaturesMapper build() {
		return new InputFixedFeaturesMapper(new FeaturesIndex(features), mappings);
	}
}
