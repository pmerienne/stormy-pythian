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

import static stormy.pythian.model.instance.FeatureType.CATEGORICAL;

@SuppressWarnings("serial")
public class CategoricalFeature extends Feature<String> {

    public CategoricalFeature(String value) {
        super(CATEGORICAL, value);
    }

    @Override
    public DecimalFeature toDecimal() {
        throw new UnsupportedOperationException("Categorical feature can't be converted to decimal feature");
    }

    @Override
    public IntegerFeature toInteger() {
        throw new UnsupportedOperationException("Categorical feature can't be converted to integer feature");
    }

    @Override
    public BooleanFeature toBoolean() {
        throw new UnsupportedOperationException("Categorical feature can't be converted to decimal feature");
    }

    @Override
    public CategoricalFeature toCategorical() {
        return new CategoricalFeature(value);
    }

    @Override
    public TextFeature toText() {
        return new TextFeature(value);
    }

    @Override
    public DateFeature toDate() {
        throw new UnsupportedOperationException("Categorical feature can't be converted to date feature");
    }

}
