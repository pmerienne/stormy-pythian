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
package stormy.pythian.service.reflect;

import static com.google.common.collect.Sets.filter;

import java.lang.reflect.Modifier;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import stormy.pythian.model.component.Component;

import com.google.common.base.Predicate;

@Service
public class ReflectionService {

	private Reflections reflections;

	@Value("#{component.base.package}")
	private String basePackage;

	@PostConstruct
	public void loadReflections() {
		reflections = new Reflections(basePackage);
	}

	public Set<Class<? extends Component>> getComponentClasses() {
		return filter(reflections.getSubTypesOf(Component.class), new Predicate<Class<?>>() {
			public boolean apply(Class<?> input) {
				return !Modifier.isAbstract(input.getModifiers());
			}
		});
	}
}
