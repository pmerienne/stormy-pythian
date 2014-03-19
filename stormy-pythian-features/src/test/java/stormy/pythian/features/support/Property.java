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
package stormy.pythian.features.support;

public class Property {

    public final String name;
    public final Type type;
    public final String value;

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
        this.type = null;
    }

    public Property(String name, Type type, String value) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public static enum Type {
        String, Integer, Double, Boolean, Enum, Decimal
    }
}
