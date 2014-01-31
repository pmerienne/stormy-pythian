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

import static org.fest.assertions.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import stormy.pythian.model.component.Component;
import stormy.pythian.service.description.ClassRepository;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("serial")
public class ClassRepositoryTest {

	@InjectMocks
	private ClassRepository service;

	@Test
	public void should_retieve_components() {
		// Given
		setField(service, "basePackage", ClassRepositoryTest.class.getPackage().getName());
		service.loadReflections();

		// When
		Set<Class<? extends Component>> actualClasses = service.getComponentClasses();

		// Then
		assertThat(actualClasses).contains(TestComponent1.class, TestComponent2.class);
	}

	@Test
	public void should_not_retrieve_abstract_components() {
		// Given
		setField(service, "basePackage", ClassRepositoryTest.class.getPackage().getName());
		service.loadReflections();

		// When
		Set<Class<? extends Component>> actualClasses = service.getComponentClasses();

		// Then
		assertThat(actualClasses).excludes(AbstractComponent.class);
	}

	private static class TestComponent1 implements Component {
		@Override
		public void init() {
		}
	}

	private static class TestComponent2 implements Component {
		@Override
		public void init() {
		}
	}

	private static abstract class AbstractComponent implements Component {
		@Override
		public void init() {
		}
	}
}
