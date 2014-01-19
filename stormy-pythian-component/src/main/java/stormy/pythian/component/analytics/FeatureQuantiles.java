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

import static stormy.pythian.component.analytics.Constants.MEDIAN_FEATURE;
import static stormy.pythian.component.analytics.Constants.QUANTILE_75th_FEATURE;
import static stormy.pythian.component.analytics.Constants.QUANTILE_90th_FEATURE;
import static stormy.pythian.component.analytics.Constants.QUANTILE_95th_FEATURE;
import static stormy.pythian.component.analytics.Constants.QUANTILE_99th9_FEATURE;
import static stormy.pythian.component.analytics.Constants.QUANTILE_99th_FEATURE;
import static stormy.pythian.model.annotation.ComponentType.ANALYTICS;
import storm.trident.Stream;
import stormy.pythian.component.analytics.StatisticAggregator.AggregableStatistic;
import stormy.pythian.component.analytics.tdigest.TDigest;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.Mapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.instance.OutputFeaturesMapper;

@Documentation(name = "Feature quantiles", type = ANALYTICS)
public class FeatureQuantiles extends AbstractFeatureStatistic<TDigest> {

	private static final long serialVersionUID = 1L;
	
	@OutputStream(name = "out", from = "in", newFeatures = { //
	@ExpectedFeature(name = MEDIAN_FEATURE, type = Double.class), //
			@ExpectedFeature(name = QUANTILE_75th_FEATURE, type = Double.class), //
			@ExpectedFeature(name = QUANTILE_90th_FEATURE, type = Double.class), //
			@ExpectedFeature(name = QUANTILE_95th_FEATURE, type = Double.class), //
			@ExpectedFeature(name = QUANTILE_99th_FEATURE, type = Double.class), //
			@ExpectedFeature(name = QUANTILE_99th9_FEATURE, type = Double.class) //
	})
	private Stream out;

	@Mapper(stream = "out")
	private OutputFeaturesMapper outputMapper;

	@Property(name = "Compression", description = "How should accuracy be traded for size? A value of N here will give quantile errors almost always less than 3/N with considerably smaller errors expected for extreme quantiles. Conversely, you should expect to track about 5 N centroids for this accuracy.")
	private double compression = 100.0;

	@Override
	public void init() {
		AggregableStatistic<TDigest> aggregableStatistic = new AggregableQuantiles(outputMapper, compression);
		out = initOutputStream(aggregableStatistic);
	}

}
