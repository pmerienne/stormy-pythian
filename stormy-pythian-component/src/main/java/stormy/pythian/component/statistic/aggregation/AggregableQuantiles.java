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
package stormy.pythian.component.statistic.aggregation;

import static java.util.Arrays.asList;
import static stormy.pythian.component.statistic.aggregation.Constants.MEDIAN_FEATURE;
import static stormy.pythian.component.statistic.aggregation.Constants.QUANTILE_75th_FEATURE;
import static stormy.pythian.component.statistic.aggregation.Constants.QUANTILE_90th_FEATURE;
import static stormy.pythian.component.statistic.aggregation.Constants.QUANTILE_95th_FEATURE;
import static stormy.pythian.component.statistic.aggregation.Constants.QUANTILE_99th9_FEATURE;
import static stormy.pythian.component.statistic.aggregation.Constants.QUANTILE_99th_FEATURE;

import java.util.HashMap;
import java.util.Map;

import org.apache.mahout.common.RandomUtils;

import stormy.pythian.component.statistic.aggregation.StatisticAggregator.AggregableStatistic;
import stormy.pythian.component.statistic.tdigest.TDigest;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFixedFeaturesMapper;

public class AggregableQuantiles implements AggregableStatistic<TDigest> {

	private static final long serialVersionUID = 845845674309635484L;

	private final OutputFixedFeaturesMapper mapper;
	private final double compression;

	public AggregableQuantiles(OutputFixedFeaturesMapper mapper, double compression) {
		this.mapper = mapper;
		this.compression = compression;
	}

	@Override
	public TDigest init(Number feature) {
		TDigest quantilesState = new TDigest(compression);
		quantilesState.add(feature.doubleValue());
		return quantilesState;
	}

	@Override
	public TDigest combine(TDigest val1, TDigest val2) {
		if (val1 == null && val2 == null) {
			return null;
		} else if (val1 == null) {
			return val2;
		} else if (val2 == null) {
			return val1;
		}

		TDigest merged = TDigest.merge(compression, asList(val1, val2), RandomUtils.getRandom());
		return merged;
	}

	@Override
	public TDigest zero() {
		return null;
	}

	@Override
	public Instance update(Instance original, TDigest digest) {
		Map<String, Object> newFeatures = new HashMap<>(6);
		newFeatures.put(MEDIAN_FEATURE, getQuantile(digest, 0.5));
		newFeatures.put(QUANTILE_75th_FEATURE, getQuantile(digest, 0.75));
		newFeatures.put(QUANTILE_90th_FEATURE, getQuantile(digest, 0.9));
		newFeatures.put(QUANTILE_95th_FEATURE, getQuantile(digest, 0.95));
		newFeatures.put(QUANTILE_99th_FEATURE, getQuantile(digest, 0.99));
		newFeatures.put(QUANTILE_99th9_FEATURE, getQuantile(digest, 0.999));

		Instance newInstance = original.withFeatures(mapper, newFeatures);
		return newInstance;
	}

	private Double getQuantile(TDigest digest, double q) {
		if (digest == null || digest.centroidCount() <= 1) {
			return null;
		} else {
			return digest.quantile(q);
		}

	}
}
