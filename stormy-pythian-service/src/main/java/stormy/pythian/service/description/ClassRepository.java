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
package stormy.pythian.service.description;

import static com.google.common.collect.Sets.filter;

import java.lang.reflect.Modifier;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.reflections.Reflections;
import org.springframework.stereotype.Repository;

import stormy.pythian.model.component.Component;
import stormy.pythian.model.component.PythianState;

import com.google.common.base.Predicate;

@Repository
public class ClassRepository {

    private static final String STORMY_PYTHIAN_PACKAGE = "stormy.pythian";
	private Reflections reflections;

    @PostConstruct
    public void loadReflections() {
        reflections = new Reflections(STORMY_PYTHIAN_PACKAGE);
    }

    public Set<Class<? extends Component>> getComponentClasses() {
        return filter(reflections.getSubTypesOf(Component.class), new Predicate<Class<?>>() {
            public boolean apply(Class<?> input) {
                return !Modifier.isAbstract(input.getModifiers());
            }
        });
    }

	public Set<Class<? extends PythianState>> getStateClasses() {
        return filter(reflections.getSubTypesOf(PythianState.class), new Predicate<Class<?>>() {
            public boolean apply(Class<?> input) {
                return !Modifier.isAbstract(input.getModifiers());
            }
        });
	}
}
