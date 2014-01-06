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
import java.util.UUID;

public class PythianToplogyConfiguration {

	private String id;

	public final List<ComponentConfiguration> components;
	public final List<ConnectionConfiguration> connections;

	public PythianToplogyConfiguration() {
		this.id = UUID.randomUUID().toString();
		this.components = new ArrayList<>();
		this.connections = new ArrayList<>();
	}

	public PythianToplogyConfiguration(String id,
			List<ComponentConfiguration> components,
			List<ConnectionConfiguration> connections) {
		this.id = id;
		this.components = components;
		this.connections = connections;
	}

	public PythianToplogyConfiguration(String id) {
		this.id = id;
		this.components = new ArrayList<>();
		this.connections = new ArrayList<>();
	}

	public void ensureId() {
		if (this.id == null) {
			this.id = UUID.randomUUID().toString();
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((components == null) ? 0 : components.hashCode());
		result = prime * result
				+ ((connections == null) ? 0 : connections.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PythianToplogyConfiguration other = (PythianToplogyConfiguration) obj;
		if (components == null) {
			if (other.components != null)
				return false;
		} else if (!components.equals(other.components))
			return false;
		if (connections == null) {
			if (other.connections != null)
				return false;
		} else if (!connections.equals(other.connections))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PythianToplogyConfiguration [id=" + id + ", components="
				+ components + ", connections=" + connections + "]";
	}

}
