package stormy.pythian.service.reflect;

import static org.fest.assertions.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import stormy.pythian.model.component.Component;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("serial")
public class ReflectionServiceTest {

	@InjectMocks
	private ReflectionService service;

	@Test
	public void should_retieve_components() {
		// Given
		setField(service, "basePackage", ReflectionServiceTest.class.getPackage().getName());
		service.loadReflections();

		// When
		Set<Class<? extends Component>> actualClasses = service.getComponentClasses();

		// Then
		assertThat(actualClasses).contains(TestComponent1.class, TestComponent2.class);
	}

	@Test
	public void should_not_retrieve_abstract_components() {
		// Given
		setField(service, "basePackage", ReflectionServiceTest.class.getPackage().getName());
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
