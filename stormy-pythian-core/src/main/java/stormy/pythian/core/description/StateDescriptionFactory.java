package stormy.pythian.core.description;

import static org.reflections.ReflectionUtils.getFields;
import static org.reflections.ReflectionUtils.withAnnotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import storm.trident.state.StateFactory;
import stormy.pythian.model.annotation.State;

public class StateDescriptionFactory {

	@SuppressWarnings("unchecked")
	public List<StateDescription> createDescriptions(Class<?> componentClass) {
		List<StateDescription> descriptions = new ArrayList<>();

		Set<Field> fields = getFields(componentClass, withAnnotation(State.class));

		for (Field field : fields) {
			checkSupportedType(field);

			State annotation = field.getAnnotation(State.class);

			StateDescription description = new StateDescription(annotation.name(), annotation.description());
			descriptions.add(description);
		}

		ensureNoDuplicatedStateName(descriptions);
		return descriptions;
	}

	private void ensureNoDuplicatedStateName(List<StateDescription> descriptions) {
		Set<String> duplicatedNames = new HashSet<>();
		Set<String> uniqueNames = new HashSet<>();

		for (StateDescription description : descriptions) {
			if (uniqueNames.contains(description.getName())) {
				duplicatedNames.add(description.getName());
			} else {
				uniqueNames.add(description.getName());
			}
		}

		if (!duplicatedNames.isEmpty()) {
			throw new IllegalArgumentException("@State should have unique name. Found duplicates : " + Lists.newArrayList(duplicatedNames));
		}
	}

	private void checkSupportedType(Field field) {
		Class<?> type = field.getType();
		if (!StateFactory.class.equals(type)) {
			throw new IllegalArgumentException(State.class + " annotation must be used on a " + StateFactory.class + " class");
		}
	}

}
