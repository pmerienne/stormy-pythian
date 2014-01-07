package stormy.pythian.core.ioc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import stormy.pythian.core.description.ComponentDescriptionFactory;
import stormy.pythian.core.description.FeatureDescriptionFactory;
import stormy.pythian.core.description.InputStreamDescriptionFactory;
import stormy.pythian.core.description.OutputStreamDescriptionFactory;
import stormy.pythian.core.description.PropertyDescriptionFactory;

@Configuration
public class StormyPythianCoreConfig {

	@Bean
	public ComponentDescriptionFactory componentDescriptionFactory() {
		return new ComponentDescriptionFactory();
	}

	@Bean
	public PropertyDescriptionFactory propertyDescriptionFactory() {
		return new PropertyDescriptionFactory();
	}

	@Bean
	public InputStreamDescriptionFactory inputStreamDescriptionFactory() {
		return new InputStreamDescriptionFactory();
	}

	@Bean
	public OutputStreamDescriptionFactory outputStreamDescriptionFactory() {
		return new OutputStreamDescriptionFactory();
	}

	@Bean
	public FeatureDescriptionFactory featureDescriptionFactory() {
		return new FeatureDescriptionFactory();
	}
}