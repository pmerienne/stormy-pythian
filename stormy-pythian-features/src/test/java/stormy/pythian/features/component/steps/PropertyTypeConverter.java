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
package stormy.pythian.features.component.steps;

import static org.apache.commons.lang.StringUtils.isBlank;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

@SuppressWarnings({ "rawtypes", "unchecked" })
public enum PropertyTypeConverter {
    STRING {
        @Override
        public Object convert(String type, String value) {
            return value;
        }
    },
    DOUBLE {
        @Override
        public Object convert(String type, String value) {
            return isBlank(value) ? null : Double.valueOf(value);
        }
    },
    INTEGER {
        @Override
        public Object convert(String type, String value) {
            return isBlank(value) ? null : Integer.valueOf(value);
        }
    },
    LONG {
        @Override
        public Object convert(String type, String value) {
            return isBlank(value) ? null : Long.valueOf(value);
        }
    },
    BOOLEAN {
        @Override
        public Object convert(String type, String value) {
            return isBlank(value) ? null : Boolean.valueOf(value);
        }
    },
    DATE {
        @Override
        public Object convert(String type, String value) {
            return DateTime.parse(value).toDate();
        }
    },
    ENUM {
        @Override
        public Object convert(String type, String value) {
            String enumName = type.split(":")[1];
            try {
                Class enumType = Class.forName(enumName);
                return Enum.valueOf(enumType, value);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("No enum found for name " + enumName);
            }
        }
    };

    public abstract Object convert(String type, String value);

    public Object convert(String value) {
        return convert(null, value);
    }

    public static PropertyTypeConverter from(String type) {
        if (StringUtils.startsWithIgnoreCase(type, ENUM.name() + ":")) {
            return ENUM;
        }

        for (PropertyTypeConverter converter : PropertyTypeConverter.values()) {
            if (StringUtils.equalsIgnoreCase(converter.name(), type)) {
                return converter;
            }
        }
        throw new IllegalArgumentException("No type converter found for " + type);
    }
}
