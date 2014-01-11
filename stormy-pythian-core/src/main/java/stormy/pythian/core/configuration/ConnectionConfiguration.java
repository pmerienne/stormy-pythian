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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ConnectionConfiguration {

	public String from;
	public String fromStreamName;

	public String to;
	public String toStreamName;

	public ConnectionConfiguration() {
	}

	public ConnectionConfiguration(String from, String fromStreamName, String to, String toStreamName) {
		this.from = from;
		this.fromStreamName = fromStreamName;
		this.to = to;
		this.toStreamName = toStreamName;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(from).append(fromStreamName).append(to).append(toStreamName).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ConnectionConfiguration)) {
			return false;
		}

		ConnectionConfiguration other = (ConnectionConfiguration) obj;

		return new EqualsBuilder() //
				.append(this.from, other.from) //
				.append(this.fromStreamName, other.fromStreamName) //
				.append(this.to, other.to) //
				.append(this.toStreamName, other.toStreamName) //
				.isEquals();
	}

	@Override
	public String toString() {
		return "ConnectionConfiguration [from=" + from + ", fromStreamName=" + fromStreamName + ", to=" + to + ", toStreamName=" + toStreamName + "]";
	}

}
