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
package stormy.pythian.core.description;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class PropertyDescription {

	public String name;
	public String description;

	public boolean mandatory;
	public Class<?> type;

	public PropertyDescription() {
	}

	public PropertyDescription(String name, String description, boolean mandatory, Class<?> type) {
		this.name = name;
		this.description = description;
		this.mandatory = mandatory;
		this.type = type;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).append(description).append(mandatory).append(type).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PropertyDescription)) {
			return false;
		}

		PropertyDescription that = (PropertyDescription) obj;
		return new EqualsBuilder().append(this.name, that.name).append(this.description, that.description).append(this.mandatory, that.mandatory).append(this.type, that.type).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(name).append(description).append(mandatory).append(type).toString();
	}

}
