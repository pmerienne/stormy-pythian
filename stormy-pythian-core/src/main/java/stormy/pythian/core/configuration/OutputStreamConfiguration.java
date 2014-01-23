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

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormy.pythian.core.description.OutputStreamDescription;
import stormy.pythian.model.annotation.MappingType;

public class OutputStreamConfiguration {

	public OutputStreamDescription descriptor;

	public Map<String, String> mappings = new HashMap<>();
	private final List<String> selectedFeatures;

	public OutputStreamConfiguration(OutputStreamDescription descriptor, Map<String, String> mappings) {
		this.descriptor = descriptor;
		this.mappings = mappings;
		this.selectedFeatures = null;
	}

	public OutputStreamConfiguration(OutputStreamDescription descriptor, List<String> selectedFeatures) {
		this.descriptor = descriptor;
		this.mappings = null;
		this.selectedFeatures = selectedFeatures;
	}

	public String getInputStreamSource() {
		return descriptor.getFrom();
	}

	public boolean hasInputStreamSource() {
		return !isEmpty(descriptor.getFrom());
	}

	public String getStreamName() {
		return descriptor.getName();
	}

	public MappingType getMappingType() {
		return descriptor.getMappingType();
	}

	public Collection<String> getNewFeatures() {
		switch (descriptor.getMappingType()) {
		case FIXED_FEATURES:
			return mappings.values();
		case USER_SELECTION:
			return selectedFeatures;
		default:
			throw new IllegalStateException("Mapping type " + descriptor.getMappingType() + " isn't supported!");
		}
	}
}
