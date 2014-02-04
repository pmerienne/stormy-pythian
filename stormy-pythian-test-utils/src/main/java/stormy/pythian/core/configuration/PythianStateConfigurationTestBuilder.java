package stormy.pythian.core.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import stormy.pythian.core.description.PythianStateDescription;

public class PythianStateConfigurationTestBuilder {

	private String id = UUID.randomUUID().toString();
	private PythianStateDescription description;
	private List<PropertyConfiguration> properties = new ArrayList<>();

	public static PythianStateConfigurationTestBuilder stateConfiguration(PythianStateDescription description) {
		PythianStateConfigurationTestBuilder builder = new PythianStateConfigurationTestBuilder();
		builder.description = description;
		return builder;
	}

	public PythianStateConfigurationTestBuilder with(String name, Object value) {
		this.properties.add(new PropertyConfiguration(name, value));
		return this;
	}

	public PythianStateConfiguration build() {
		PythianStateConfiguration configuration = new PythianStateConfiguration(id, description, properties);
		return configuration;
	}
}
