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

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@SuppressWarnings("serial")
public abstract class Feature<T> implements Serializable {

    protected final T value;
    protected final FeatureType type;

    protected Feature(FeatureType type, T value) {
        this.type = type;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean isAbsent() {
        return value != null;
    }

    public FeatureType getType() {
        return type;
    }

    public Feature<?> to(FeatureType type) {
        switch (type) {
            case BOOLEAN:
                return toBoolean();
            case CATEGORICAL:
                return toCategorical();
            case DATE:
                return toDate();
            case DECIMAL:
                return toDecimal();
            case INTEGER:
                return toInteger();
            case TEXT:
                return toText();
            default:
                throw new UnsupportedOperationException("Conversion from " + this.type + " to " + type + " is not supported");
        }
    }

    public abstract DecimalFeature toDecimal();

    public abstract IntegerFeature toInteger();

    public abstract BooleanFeature toBoolean();

    public abstract CategoricalFeature toCategorical();

    public abstract TextFeature toText();

    public abstract DateFeature toDate();

    public Double decimalValue() {
        return this.toDecimal().getValue();
    }

    public Long integerValue() {
        return this.toInteger().getValue();
    }

    public Boolean booleanValue() {
        return this.toBoolean().getValue();
    }

    public String categoricalValue() {
        return this.toCategorical().getValue();
    }

    public String textValue() {
        return this.toText().getValue();
    }

    public Date dateValue() {
        return this.toDate().getValue();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(value).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Feature) {
            Feature<?> other = (Feature<?>) obj;
            return new EqualsBuilder().append(this.type, other.type).append(this.value, other.value).isEquals();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Feature [type=" + type + ", value=" + value + "]";
    }

}
