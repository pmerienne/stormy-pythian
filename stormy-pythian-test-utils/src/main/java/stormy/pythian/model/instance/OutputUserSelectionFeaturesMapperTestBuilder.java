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
package stormy.pythian.model.instance;

import static java.util.Arrays.asList;
import java.util.ArrayList;
import java.util.List;

public class OutputUserSelectionFeaturesMapperTestBuilder {

    private final List<String> allFeatures = new ArrayList<>();
    private final List<String> newFeatures = new ArrayList<>();

    public static OutputUserSelectionFeaturesMapperTestBuilder outputUserSelectionFeaturesMapper() {
        return new OutputUserSelectionFeaturesMapperTestBuilder();
    }

    public static OutputUserSelectionFeaturesMapperTestBuilder outputUserSelectionFeaturesMapper(String... allFeatures) {
        OutputUserSelectionFeaturesMapperTestBuilder builder = new OutputUserSelectionFeaturesMapperTestBuilder();
        return builder.with(allFeatures);
    }

    public static OutputUserSelectionFeaturesMapperTestBuilder outputUserSelectionFeaturesMapper(List<String> allFeatures) {
        OutputUserSelectionFeaturesMapperTestBuilder builder = new OutputUserSelectionFeaturesMapperTestBuilder();
        builder.allFeatures.addAll(allFeatures);
        return builder;
    }

    public OutputUserSelectionFeaturesMapperTestBuilder with(String... allFeatures) {
        this.allFeatures.addAll(asList(allFeatures));
        return this;
    }

    public OutputUserSelectionFeaturesMapperTestBuilder select(String... newFeatures) {
        this.newFeatures.addAll(asList(newFeatures));
        return this;
    }

    public OutputUserSelectionFeaturesMapperTestBuilder select(List<String> newFeatures) {
        this.newFeatures.addAll(newFeatures);
        return this;
    }

    public OutputUserSelectionFeaturesMapper build() {
        return new OutputUserSelectionFeaturesMapper(new FeaturesIndex(allFeatures), newFeatures);
    }
}
