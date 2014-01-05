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
