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

import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.OutputStream;

public class FeatureDescriptionFactory {

	public FeatureDescription createDescription(ExpectedFeature feature) {
		return new FeatureDescription(feature.name(), feature.type());
	}

	public List<FeatureDescription> createDescriptions(InputStream inputStream) {
		List<FeatureDescription> descriptors = new ArrayList<>();

		ExpectedFeature[] expectedFeatures = inputStream.expectedFeatures();
		if (expectedFeatures != null) {
			for (ExpectedFeature feature : expectedFeatures) {
				descriptors.add(createDescription(feature));
			}
		}

		return descriptors;
	}

	public List<FeatureDescription> createDescriptions(OutputStream outputStream) {
		List<FeatureDescription> descriptors = new ArrayList<>();

		ExpectedFeature[] expectedFeatures = outputStream.newFeatures();
		if (expectedFeatures != null) {
			for (ExpectedFeature feature : expectedFeatures) {
				descriptors.add(createDescription(feature));
			}
		}

		return descriptors;
	}
}
