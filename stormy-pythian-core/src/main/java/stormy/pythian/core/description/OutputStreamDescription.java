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

import java.util.ArrayList;
import java.util.List;

public class OutputStreamDescription {

	private final String name;
	private final String from;
	private final List<FeatureDescription> newFeatures;

	public OutputStreamDescription(String name, String from, List<FeatureDescription> newFeatures) {
		this.name = name;
		this.from = from;
		this.newFeatures = newFeatures;
	}

	public OutputStreamDescription(String name, String from) {
		this.name = name;
		this.from = from;
		this.newFeatures = new ArrayList<>();
	}

	public OutputStreamDescription(String name, List<FeatureDescription> newFeatures) {
		this.name = name;
		this.from = null;
		this.newFeatures = newFeatures;
	}

	public String getName() {
		return name;
	}

	public String getFrom() {
		return from;
	}

	public List<FeatureDescription> getNewFeatures() {
		return newFeatures;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((newFeatures == null) ? 0 : newFeatures.hashCode());
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
		OutputStreamDescription other = (OutputStreamDescription) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (newFeatures == null) {
			if (other.newFeatures != null)
				return false;
		} else if (!newFeatures.equals(other.newFeatures))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OutputStreamDescription [name=" + name + ", from=" + from + ", newFeatures=" + newFeatures + "]";
	}

}
