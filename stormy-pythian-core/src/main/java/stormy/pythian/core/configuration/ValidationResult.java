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
package stormy.pythian.core.configuration;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

    private final String name;
    private final List<String> errors;

    public ValidationResult(String name) {
        this.name = name;
        this.errors = new ArrayList<String>();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void add(String error) {
        errors.add(error);
    }

    public void add(String placeholder, Object... parameters) {
        this.add(String.format(placeholder, parameters));
    }

    public List<String> getErrors() {
        return errors;
    }

    public void include(ValidationResult child) {
        for (String childError : child.getErrors()) {
            this.add(new StringBuilder(child.name).append(" > ").append(childError).toString());
        }
    }

    public String print() {
        StringBuilder sb = new StringBuilder(name).append("\n");
        for (String error : errors) {
            sb.append(error).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ValidationResult [name=" + name + ", errors=" + errors + "]";
    }

}
