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

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static stormy.pythian.core.description.FeatureDescription.EXTRACT_NAME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormy.pythian.core.description.OutputStreamDescription;

public class OutputStreamConfiguration {

	public OutputStreamDescription descriptor;

	public Map<String, String> mappings = new HashMap<>();

	public OutputStreamConfiguration() {
	}

	public OutputStreamConfiguration(OutputStreamDescription descriptor) {
		this.descriptor = descriptor;
	}

	public OutputStreamConfiguration(OutputStreamDescription descriptor, Map<String, String> mappings) {
		this.descriptor = descriptor;
		this.mappings = mappings;
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

	public List<String> getNewFeatureNames() {
		return newArrayList(transform(descriptor.getNewFeatures(), EXTRACT_NAME));
	}
}
