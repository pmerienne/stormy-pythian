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

import static stormy.pythian.model.instance.FeatureType.INTEGER;
import java.util.Date;

@SuppressWarnings("serial")
public class IntegerFeature extends Feature<Long> {

    public IntegerFeature(Long value) {
        super(INTEGER, value);
    }

    @Override
    public DecimalFeature toDecimal() {
        return new DecimalFeature(value == null ? null : value.doubleValue());
    }

    @Override
    public IntegerFeature toInteger() {
        return new IntegerFeature(value);
    }

    @Override
    public BooleanFeature toBoolean() {
        return new BooleanFeature(value == null ? null : value > 0);
    }

    @Override
    public CategoricalFeature toCategorical() {
        return new CategoricalFeature(value == null ? null : value.toString());
    }

    @Override
    public TextFeature toText() {
        return new TextFeature(value == null ? null : value.toString());
    }

    @Override
    public DateFeature toDate() {
        return new DateFeature(value == null ? null : new Date(value));
    }

}
