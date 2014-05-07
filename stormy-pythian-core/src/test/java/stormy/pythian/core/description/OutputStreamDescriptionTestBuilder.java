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

public class OutputStreamDescriptionTestBuilder {

    private String name = "";
    private String from = "";
    private List<FeatureDescription> newFeatures = new ArrayList<>();
    private MappingType type = NONE;

    public static OutputStreamDescriptionTestBuilder outputStreamDescription() {
        return new OutputStreamDescriptionTestBuilder();
    }

    public OutputStreamDescriptionTestBuilder type(MappingType type) {
        this.type = type;
        return this;
    }

    public OutputStreamDescriptionTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public OutputStreamDescriptionTestBuilder from(String from) {
        this.from = from;
        return this;
    }

    public OutputStreamDescriptionTestBuilder with(FeatureDescription feature) {
        this.newFeatures.add(feature);
        return this;
    }

    public OutputStreamDescriptionTestBuilder withFeature(String name, FeatureType type) {
        return this.with(new FeatureDescription(name, type));
    }

    public OutputStreamDescription build() {
        return new OutputStreamDescription(name, from, type, newFeatures);
    }
}
