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
package stormy.pythian.component.analytics;

import storm.trident.Stream;
import stormy.pythian.component.analytics.AggregableMean.MeanState;
import stormy.pythian.component.analytics.StatisticAggregator.AggregableStatistic;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.Mapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.instance.OutputFeaturesMapper;

@Documentation(name = "Feature mean", description = "Compute mean of each feature occurence")
public class FeatureMean extends AbstractFeatureStatistic<MeanState> {

	private static final long serialVersionUID = -5312700259983804231L;

	public static final String MEAN_FEATURE = "Feature mean";

	@OutputStream(name = "out", from = "in", newFeatures = { @ExpectedFeature(name = MEAN_FEATURE, type = Double.class) })
	private Stream out;

	@Mapper(stream = "out")
	private OutputFeaturesMapper outputMapper;

	@Override
	public void init() {
		AggregableStatistic<MeanState> aggregableStatistic = new AggregableMean(outputMapper, MEAN_FEATURE);
		out = initOutputStream(aggregableStatistic);
	}

}
