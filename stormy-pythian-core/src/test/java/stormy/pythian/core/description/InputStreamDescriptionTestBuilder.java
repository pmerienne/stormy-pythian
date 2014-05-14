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

import static stormy.pythian.model.annotation.MappingType.NONE;
import java.util.ArrayList;
import java.util.List;
import stormy.pythian.model.annotation.MappingType;
import stormy.pythian.model.instance.FeatureType;

public class InputStreamDescriptionTestBuilder {

    private String name = "";
    private MappingType type = NONE;
    private List<FeatureDescription> expectedFeatures = new ArrayList<>();
    private boolean mandatory = true;

    public static InputStreamDescriptionTestBuilder inputStreamDescription() {
        return new InputStreamDescriptionTestBuilder();
    }

    public InputStreamDescriptionTestBuilder type(MappingType type) {
        this.type = type;
        return this;
    }

    public InputStreamDescriptionTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public InputStreamDescriptionTestBuilder mandatory(boolean mandatory) {
        this.mandatory = mandatory;
        return this;
    }

    public InputStreamDescriptionTestBuilder with(FeatureDescription feature) {
        this.expectedFeatures.add(feature);
        return this;
    }

    public InputStreamDescriptionTestBuilder withFeature(String name, FeatureType type) {
        return this.with(new FeatureDescription(name, type));
    }

    public InputStreamDescription build() {
        return new InputStreamDescription(name, type, expectedFeatures, mandatory);
    }
}
