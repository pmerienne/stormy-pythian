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

public class ValidationResultTestBuilder {

    private String name;
    private List<String> errors = new ArrayList<>();

    public static ValidationResultTestBuilder validationResult() {
        return new ValidationResultTestBuilder();
    }

    public static ValidationResultTestBuilder validationResult(String name) {
        ValidationResultTestBuilder validationResultTestBuilder = new ValidationResultTestBuilder();
        return validationResultTestBuilder.name(name);
    }

    public ValidationResultTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ValidationResultTestBuilder with(String error) {
        this.errors.add(error);
        return this;
    }

    public ValidationResult build() {
        ValidationResult validationResult = new ValidationResult(name);
        for (String error : errors) {
            validationResult.add(error);
        }
        return validationResult;
    }

}
