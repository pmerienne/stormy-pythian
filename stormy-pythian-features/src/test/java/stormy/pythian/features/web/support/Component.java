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
package stormy.pythian.features.web.support;

public class Component {

    public final String type;
    public final String component;
    public final String name;
    public final int x;
    public final int y;

    public Component(String type, String component, String name, int x, int y) {
        this.type = type;
        this.component = component;
        this.name = name;
        this.x = x;
        this.y = y;
    }

}
