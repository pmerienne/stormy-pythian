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

import static com.google.common.collect.Iterables.tryFind;

import java.util.ArrayList;
import java.util.List;

import stormy.pythian.core.description.ComponentDescription;

import com.google.common.base.Predicate;

public class ComponentConfiguration {

	public String id;

	public ComponentDescription descriptor;

	public List<PropertyConfiguration> properties = new ArrayList<>();
	public List<InputStreamConfiguration> inputStreams = new ArrayList<>();
	public List<OutputStreamConfiguration> outputStreams = new ArrayList<>();
	private List<StateConfiguration> states = new ArrayList<>();

	public ComponentConfiguration() {
	}

	public ComponentConfiguration(String id) {
		this.id = id;
	}

	public ComponentConfiguration(String id, ComponentDescription descriptor) {
		this.id = id;
		this.descriptor = descriptor;
	}

	public ComponentConfiguration(String id, ComponentDescription descriptor, List<PropertyConfiguration> properties, List<InputStreamConfiguration> inputStreams,
			List<OutputStreamConfiguration> outputStreams) {
		this.id = id;
		this.descriptor = descriptor;
		this.properties = properties;
		this.inputStreams = inputStreams;
		this.outputStreams = outputStreams;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<InputStreamConfiguration> getInputStreams() {
		return inputStreams;
	}

	public void setInputStreams(List<InputStreamConfiguration> inputStreams) {
		this.inputStreams = inputStreams;
	}

	public List<OutputStreamConfiguration> getOutputStreams() {
		return outputStreams;
	}

	public void setOutputStreams(List<OutputStreamConfiguration> outputStreams) {
		this.outputStreams = outputStreams;
	}

	public List<StateConfiguration> getStates() {
		return states;
	}

	public void setStates(List<StateConfiguration> states) {
		this.states = states;
	}

	public OutputStreamConfiguration findOutputStreamByName(final String name) {
		return tryFind(outputStreams, new Predicate<OutputStreamConfiguration>() {
			public boolean apply(OutputStreamConfiguration candidate) {
				return name.equals(candidate.getStreamName());
			}
		}).orNull();
	}

}
