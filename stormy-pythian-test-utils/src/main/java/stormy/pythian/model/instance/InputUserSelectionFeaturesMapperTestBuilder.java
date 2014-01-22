package stormy.pythian.model.instance;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

public class InputUserSelectionFeaturesMapperTestBuilder {

	private final List<String> features = new ArrayList<>();
	private final List<String> selected = new ArrayList<>();

	public static InputUserSelectionFeaturesMapperTestBuilder inputFixedFeaturesMapper() {
		return new InputUserSelectionFeaturesMapperTestBuilder();
	}

	public static InputUserSelectionFeaturesMapperTestBuilder inputUserSelectionFeaturesMapper(String... features) {
		InputUserSelectionFeaturesMapperTestBuilder builder = new InputUserSelectionFeaturesMapperTestBuilder();
		return builder.with(features);
	}

	public InputUserSelectionFeaturesMapperTestBuilder with(String... features) {
		this.features.addAll(asList(features));
		return this;
	}

	public InputUserSelectionFeaturesMapperTestBuilder select(String... features) {
		this.selected.addAll(asList(features));
		return this;
	}

	public InputUserSelectionFeaturesMapper build() {
		return new InputUserSelectionFeaturesMapper(new FeaturesIndex(features), selected);
	}
}
