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
package stormy.pythian.core.description;

import static org.reflections.ReflectionUtils.getAllFields;
import static org.reflections.ReflectionUtils.withAnnotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import storm.trident.Stream;
import stormy.pythian.core.utils.ReflectionHelper;
import stormy.pythian.model.annotation.ListMapper;
import stormy.pythian.model.annotation.MappingType;
import stormy.pythian.model.annotation.NameMapper;
import stormy.pythian.model.annotation.OutputStream;

@Component
public class OutputStreamDescriptionFactory {

    @Autowired
    private FeatureDescriptionFactory featureDescriptorFactory;

    @SuppressWarnings("unchecked")
    public List<OutputStreamDescription> createOutputStreamDescriptions(Class<?> componentClass) {
        List<OutputStreamDescription> descriptions = new ArrayList<>();

        Set<Field> fields = getAllFields(componentClass, withAnnotation(OutputStream.class));
        for (Field field : fields) {
            if (field.getType() != Stream.class) {
                throw new IllegalArgumentException(OutputStream.class + " can only be applied to " + Stream.class);
            }

            OutputStream annotation = field.getAnnotation(OutputStream.class);
            OutputStreamDescription description = createOutputStreamDescription(componentClass, annotation);
            descriptions.add(description);
        }

        return descriptions;
    }

    private OutputStreamDescription createOutputStreamDescription(Class<?> componentClass, OutputStream annotation) {
        String streamName = annotation.name();

        NameMapper nameMapper = ReflectionHelper.getNameMapper(componentClass, streamName);
        ListMapper listMapper = ReflectionHelper.getListMapper(componentClass, streamName);

        if (nameMapper != null) {
            List<FeatureDescription> expectedFeatures = featureDescriptorFactory.createDescriptions(nameMapper);
            return new OutputStreamDescription(streamName, annotation.from(), MappingType.NAMED, expectedFeatures);
        } else if (listMapper != null) {
            return new OutputStreamDescription(streamName, annotation.from(), MappingType.LISTED, new ArrayList<FeatureDescription>());
        } else {
            return new OutputStreamDescription(streamName, annotation.from(), MappingType.NONE, new ArrayList<FeatureDescription>());
        }
    }

}
