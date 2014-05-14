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

import static stormy.pythian.model.instance.FeatureType.ANY;

@SuppressWarnings("serial")
public class NullFeature extends Feature<Object> {

    public NullFeature() {
        super(ANY, null);
    }

    @Override
    public DecimalFeature toDecimal() {
        return new DecimalFeature(null);
    }

    @Override
    public IntegerFeature toInteger() {
        return new IntegerFeature(null);
    }

    @Override
    public BooleanFeature toBoolean() {
        return new BooleanFeature(null);
    }

    @Override
    public CategoricalFeature toCategorical() {
        return new CategoricalFeature(null);
    }

    @Override
    public TextFeature toText() {
        return new TextFeature(null);
    }

    @Override
    public DateFeature toDate() {
        return new DateFeature(null);
    }

}
