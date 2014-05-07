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

import static stormy.pythian.model.instance.FeatureType.DATE;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

@SuppressWarnings("serial")
public class DateFeature extends Feature<Date> {

    public DateFeature(Date value) {
        super(DATE, value);
    }

    @Override
    public DecimalFeature toDecimal() {
        throw new UnsupportedOperationException("Date feature can't be converted to date feature");
    }

    @Override
    public IntegerFeature toInteger() {
        return new IntegerFeature(value == null ? null : value.getTime());
    }

    @Override
    public BooleanFeature toBoolean() {
        throw new UnsupportedOperationException("Date feature can't be converted to boolean feature");
    }

    @Override
    public CategoricalFeature toCategorical() {
        throw new UnsupportedOperationException("Date feature can't be converted to categorical feature");
    }

    @Override
    public TextFeature toText() {
        if (value == null) {
            return new TextFeature(null);
        }

        return new TextFeature(ISODateTimeFormat.dateTimeParser().withOffsetParsed().print(new DateTime(value)));
    }

    @Override
    public DateFeature toDate() {
        return new DateFeature(value);
    }

}
