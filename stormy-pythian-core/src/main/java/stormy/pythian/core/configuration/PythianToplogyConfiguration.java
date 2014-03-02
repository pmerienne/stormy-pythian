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

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class PythianToplogyConfiguration {

	private String id;
	private String name;

	private List<ComponentConfiguration> components = new ArrayList<>();
	private List<ConnectionConfiguration> connections = new ArrayList<>();

	public PythianToplogyConfiguration() {
	}

	public PythianToplogyConfiguration(String id, List<ComponentConfiguration> components, List<ConnectionConfiguration> connections) {
		this.id = id;
		this.components = components;
		this.connections = connections;
	}

	public PythianToplogyConfiguration(String id) {
		this.id = id;
	}
	

	public PythianToplogyConfiguration(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String ensureId() {
		if (this.id == null) {
			this.id = UUID.randomUUID().toString();
		}
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ComponentConfiguration> getComponents() {
		return components;
	}

	public List<ConnectionConfiguration> getConnections() {
		return connections;
	}

	public void add(ComponentConfiguration component) {
		this.components.add(component);
	}

	public void add(ConnectionConfiguration connection) {
		this.connections.add(connection);
	}

	public List<ConnectionConfiguration> findConnectionsFrom(final String componentId) {
		return newArrayList(filter(connections, new Predicate<ConnectionConfiguration>() {
			public boolean apply(ConnectionConfiguration input) {
				return input.isFrom(componentId);
			}
		}));
	}

	public ConnectionConfiguration findConnectionTo(final String componentId, final String streamName) {
		return Iterables.tryFind(connections, new Predicate<ConnectionConfiguration>() {
			public boolean apply(ConnectionConfiguration input) {
				return input.isTo(componentId, streamName);
			}
		}).orNull();
	}

	public void setComponents(List<ComponentConfiguration> components) {
		this.components = components;
	}

	public void setConnections(List<ConnectionConfiguration> connections) {
		this.connections = connections;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((components == null) ? 0 : components.hashCode());
		result = prime * result + ((connections == null) ? 0 : connections.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PythianToplogyConfiguration [id=" + id + ", name=" + name + ", components=" + components + ", connections=" + connections + "]";
	}

}
