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

import static stormy.pythian.model.instance.FeatureType.DECIMAL;

@SuppressWarnings("serial")
public class DecimalFeature extends Feature<Double> {

    public DecimalFeature(Double value) {
        super(DECIMAL, value);
    }

    @Override
    public DecimalFeature toDecimal() {
        return new DecimalFeature(value);
    }

    @Override
    public IntegerFeature toInteger() {
        return new IntegerFeature(value == null ? null : value.longValue());
    }

    @Override
    public BooleanFeature toBoolean() {
        return new BooleanFeature(value == null ? null : value > 0.0);
    }

    @Override
    public CategoricalFeature toCategorical() {
        throw new UnsupportedOperationException("Decimal feature can't be converted to categorical feature");
    }

    @Override
    public TextFeature toText() {
        return new TextFeature(value == null ? null : value.toString());
    }

    @Override
    public DateFeature toDate() {
        throw new UnsupportedOperationException("Decimal feature can't be converted to categorical date");
    }

}
