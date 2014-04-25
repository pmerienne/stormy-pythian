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
import static stormy.pythian.model.annotation.MappingType.LISTED;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import storm.trident.Stream;
import stormy.pythian.core.utils.ReflectionHelper;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.ListMapper;
import stormy.pythian.model.annotation.MappingType;
import stormy.pythian.model.annotation.NameMapper;

@Component
public class InputStreamDescriptionFactory {

    @Autowired
    private FeatureDescriptionFactory featureDescriptorFactory;

    @SuppressWarnings("unchecked")
    public List<InputStreamDescription> createInputStreamDescriptions(Class<?> componentClass) {
        List<InputStreamDescription> inputStreamDescriptions = new ArrayList<>();

        Set<Field> inputStreamFields = getAllFields(componentClass, withAnnotation(InputStream.class));
        for (Field inputStreamField : inputStreamFields) {
            if (inputStreamField.getType() != Stream.class) {
                throw new IllegalArgumentException(InputStream.class + " can only be applied to " + Stream.class);
            }

            InputStreamDescription inputStreamDescription = createInputStreamDescription(componentClass, inputStreamField);
            inputStreamDescriptions.add(inputStreamDescription);
        }

        return inputStreamDescriptions;
    }

    private InputStreamDescription createInputStreamDescription(Class<?> componentClass, Field inputStreamField) {
        InputStream annotation = inputStreamField.getAnnotation(InputStream.class);
        String streamName = annotation.name();

        NameMapper nameMapper = ReflectionHelper.getNameMapper(componentClass, streamName);
        ListMapper listMapper = ReflectionHelper.getListMapper(componentClass, streamName);

        if (nameMapper != null) {
            List<FeatureDescription> expectedFeatures = featureDescriptorFactory.createDescriptions(nameMapper);
            return new InputStreamDescription(streamName, MappingType.NAMED, expectedFeatures);
        } else if (listMapper != null) {
            return new InputStreamDescription(streamName, LISTED, new ArrayList<FeatureDescription>());
        } else {
            return new InputStreamDescription(streamName, MappingType.NONE);
        }
    }
}
