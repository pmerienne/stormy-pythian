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
package stormy.pythian.core.utils;

import static org.apache.commons.lang.reflect.FieldUtils.readField;
import static org.apache.commons.lang.reflect.FieldUtils.writeField;
import static org.reflections.ReflectionUtils.getFields;
import static org.reflections.ReflectionUtils.withAnnotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.reflect.FieldUtils;
import org.reflections.ReflectionUtils;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import stormy.pythian.core.configuration.PropertyConfiguration;
import stormy.pythian.model.annotation.Configuration;
import stormy.pythian.model.annotation.FeaturesMapper;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.annotation.Topology;
import stormy.pythian.model.component.Component;
import backtype.storm.Config;

import com.google.common.base.Predicate;

public class ReflectionHelper {

	@SuppressWarnings("unchecked")
	public static List<Stream> getOutputStreams(Object obj) {
		List<Stream> streams = new ArrayList<>();

		Set<Field> fields = getFields(obj.getClass(), ReflectionUtils.withAnnotation(OutputStream.class));
		for (Field field : fields) {
			try {
				Object candidate = readField(field, obj, true);
				if (candidate instanceof Stream) {
					streams.add((Stream) candidate);
				}
			} catch (IllegalAccessException e) {
			}
		}

		return streams;
	}

	@SuppressWarnings("unchecked")
	public static Stream getOutputStream(Object obj, String name) {
		try {
			Set<Field> fields = getFields(obj.getClass(), withOutputStream(name));
			if (fields != null && !fields.isEmpty()) {
				Field field = fields.iterator().next();
				Object candidate = FieldUtils.readField(field, obj, true);
				return (Stream) candidate;
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to get output stream " + name + " from " + obj, e);
		}
		throw new IllegalArgumentException("Unable to get output stream " + name + " from " + obj);
	}

	@SuppressWarnings("unchecked")
	public static List<String> getInputStreamNames(Class<? extends Component> componentClazz) {
		List<String> streams = new ArrayList<>();
		// TODO : better with getAnnotations
		Set<Field> fields = getFields(componentClazz, withAnnotation(InputStream.class));
		for (Field field : fields) {
			String streamName = field.getAnnotation(InputStream.class).name();
			streams.add(streamName);
		}

		return streams;
	}

	public static void setProperties(Object obj, List<PropertyConfiguration> properties) {
		for (PropertyConfiguration property : properties) {
			setProperty(obj, property);
		}
	}

	public static void setInputStreams(Component component, Map<String, Stream> inputStreams) {
		for (Entry<String, Stream> entry : inputStreams.entrySet()) {
			setInputStream(component, entry.getKey(), entry.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	public static void setTopology(Component component, TridentTopology topology) {
		try {
			Set<Field> fields = getFields(component.getClass(), withAnnotation(Topology.class));
			for (Field field : fields) {
				writeField(field, component, topology, true);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to trident topology on component " + component.getClass(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void setConfiguration(Component component, Config config) {
		try {
			Set<Field> fields = getFields(component.getClass(), withAnnotation(Configuration.class));
			for (Field field : fields) {
				writeField(field, component, config, true);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to trident topology on component " + component.getClass(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void setFeaturesMapper(Component component, String streamName, stormy.pythian.model.instance.FeaturesMapper mapper) {
		try {
			Set<Field> fields = getFields(component.getClass(), withFeaturesMapper(streamName));
			if (fields != null && !fields.isEmpty()) {
				Field field = fields.iterator().next();
				writeField(field, component, mapper, true);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to set features mapper for " + streamName, e);
		}
	}

	@SuppressWarnings("unchecked")
	private static void setInputStream(Component component, String name, Stream stream) {
		try {
			Set<Field> fields = getFields(component.getClass(), withInputStream(name));
			if (fields != null && !fields.isEmpty()) {
				Field field = fields.iterator().next();
				writeField(field, component, stream, true);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to set stream " + name, e);
		}
	}

	@SuppressWarnings("unchecked")
	private static void setProperty(Object obj, PropertyConfiguration property) {
		try {
			Set<Field> propertyFields = getFields(obj.getClass(), withProperty(property.name));
			if (propertyFields != null && !propertyFields.isEmpty()) {
				Field propertyField = propertyFields.iterator().next();
				FieldUtils.writeField(propertyField, obj, property.value, true);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to set property " + property, e);
		}
	}

	private static <T extends AnnotatedElement> Predicate<T> withProperty(final String name) {
		return new Predicate<T>() {
			public boolean apply(T input) {
				if (input == null || !input.isAnnotationPresent(Property.class)) {
					return false;
				}

				return input.getAnnotation(Property.class).name().equals(name);
			}
		};
	}

	private static <T extends AnnotatedElement> Predicate<T> withInputStream(final String name) {
		return new Predicate<T>() {
			public boolean apply(T input) {
				if (input == null || !input.isAnnotationPresent(InputStream.class)) {
					return false;
				}

				return input.getAnnotation(InputStream.class).name().equals(name);
			}
		};
	}

	private static <T extends AnnotatedElement> Predicate<T> withOutputStream(final String name) {
		return new Predicate<T>() {
			public boolean apply(T input) {
				if (input == null || !input.isAnnotationPresent(OutputStream.class)) {
					return false;
				}

				return input.getAnnotation(OutputStream.class).name().equals(name);
			}
		};
	}

	private static <T extends AnnotatedElement> Predicate<T> withFeaturesMapper(final String streamName) {
		return new Predicate<T>() {
			public boolean apply(T input) {
				if (input == null || !input.isAnnotationPresent(FeaturesMapper.class)) {
					return false;
				}

				return input.getAnnotation(FeaturesMapper.class).stream().equals(streamName);
			}
		};
	}
}
