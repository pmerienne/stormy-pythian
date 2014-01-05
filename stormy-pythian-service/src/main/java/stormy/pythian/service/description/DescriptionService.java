package stormy.pythian.service.description;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stormy.pythian.core.description.ComponentDescription;
import stormy.pythian.core.description.ComponentDescriptionFactory;
import stormy.pythian.model.annotation.ComponentType;
import stormy.pythian.model.component.Component;
import stormy.pythian.service.reflect.ReflectionService;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

@Service
public class DescriptionService {

	@Autowired
	private ComponentDescriptionFactory descriptionFactory;

	@Autowired
	private ReflectionService reflectionService;

	public Map<ComponentType, Collection<ComponentDescription>> findAllComponentDescriptions() {
		Set<Class<? extends Component>> componentClasses = reflectionService.getComponentClasses();
		Multimap<ComponentType, ComponentDescription> componentDescriptions = HashMultimap.create();

		for (Class<? extends Component> componentClass : componentClasses) {
			ComponentDescription description = descriptionFactory.createDeclaration(componentClass);
			componentDescriptions.put(description.getType(), description);
		}

		return componentDescriptions.asMap();
	}
}
