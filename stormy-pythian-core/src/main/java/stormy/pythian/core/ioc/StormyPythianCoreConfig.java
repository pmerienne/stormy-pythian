/**
 * Copyright 2013-2015 Pierre Merienne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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