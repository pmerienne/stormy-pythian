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

public class InstanceTestBuilder {

    private Instance instance = new Instance();

    public static InstanceTestBuilder instance() {
        return new InstanceTestBuilder();
    }

    public InstanceTestBuilder label(Feature<?> label) {
        instance.label = label;
        return this;
    }

    public InstanceTestBuilder with(String name, Feature<?> feature) {
        instance.features.put(name, feature);
        return this;
    }

    public Instance build() {
        return instance;
    }
}
